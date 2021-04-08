package jpabook.jpashop.repository.query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
	private final EntityManager em;

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
	 * where 절 in을 사용함으로써 Query 최적화
	 * Query 발생 수 2번
	 * 1) OrderQueryDto 가져올 때 1번
	 * 2) OrderItemQueryDto로 변환 시 1번 -> in 절 사용으로 최적
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
	 *  findAllByDto_optimization 쿼리 최적화 (Query 2번 -> 1번)
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
		return null;
	}

}
