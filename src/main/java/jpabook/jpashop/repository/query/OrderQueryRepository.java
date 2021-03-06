package jpabook.jpashop.repository.query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.QItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import static jpabook.jpashop.domain.QDelivery.*;
import static jpabook.jpashop.domain.QMember.*;
import static jpabook.jpashop.domain.QOrder.*;
import static jpabook.jpashop.domain.QOrderItem.orderItem;
import static jpabook.jpashop.domain.item.QItem.item;

@Repository
public class OrderQueryRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Autowired
    public OrderQueryRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();

        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.query.OrderItemQueryDto(oi.order.id,i.name,oi.orderPrice,oi.count) "
                        + "from OrderItem oi "
                        + "join oi.item i "
                        + "where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.query.OrderQueryDto(o.id,m.name,o.orderDate,o.status,d.address) "
                        + "from Order o "
                        + "join o.member m "
                        + "join o.delivery d", OrderQueryDto.class
        ).getResultList();
    }

    /**
     * where ??? in??? ?????????????????? Query ?????????
     * Query ?????? ??? 2???
     * 1) OrderQueryDto ????????? ??? 1???
     * 2) OrderItemQueryDto??? ?????? ??? 1??? -> in ??? ???????????? ??????
     */
    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();
        List<Long> orderIds = result.stream()
                .map(OrderQueryDto::getOrderId)
                .collect(Collectors.toList());

        List<OrderItemQueryDto> orderItems = em.createQuery(
                "select new jpabook.jpashop.repository.query.OrderItemQueryDto(oi.order.id,i.name,oi.orderPrice,oi.count) "
                        + "from OrderItem oi "
                        + "join oi.item i "
                        + "where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));

        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
        return result;
    }

    /**
     * findAllByDto_optimization ?????? ????????? (Query 2??? -> 1???)
     */
    public List<OrderFlatDto> findAllDto_flat() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.query.OrderFlatDto(o.id, m.name,  o.orderDate,  o.status, d.address,  i.name,  oi.orderPrice, oi.count) "
                        + "from Order o "
                        + "join o.member m "
                        + "join o.delivery d "
                        + "join o.orderItems oi "
                        + "join oi.item i", OrderFlatDto.class
        ).getResultList();
    }

    public List<OrderFlatDto> findAllDto_flatUsingQueryDSL() {
        return queryFactory
                .select(new QOrderFlatDto(order.id, member.name, order.orderDate, order.status,
                        delivery.address, item.name, orderItem.orderPrice, orderItem.count))
                .from(order)
                .join(order.member, member)
                .join(order.delivery, delivery)
                .join(order.orderItems, orderItem)
                .join(orderItem.item, item)
                .fetch();
    }

}
