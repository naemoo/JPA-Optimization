package jpabook.jpashop.repository;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import jpabook.jpashop.api.OrderApiController;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

	@PersistenceContext
	private EntityManager em;

	public void save(Order order) {
		em.persist(order);
	}

	public Order findOne(Long id) {
		return em.find(Order.class, id);
	}

	public List<Order> findAll(OrderSearch orderSearch) {
		List<Order> resultList = em.createQuery("select o from Order o join o.member m "
			+ "where o.status = :status "
			+ "and m.name like :name", Order.class)
			.setParameter("status", orderSearch.getOrderStatus())
			.setParameter("name", orderSearch.getMemberName())
			.getResultList();
		return resultList;
	}

	public List<Order> findAllByString(OrderSearch orderSearch) {
		//language=JPAQL
		String jpql = "select o From Order o join o.member m";
		boolean isFirstCondition = true;
		//주문 상태 검색
		if (orderSearch.getOrderStatus() != null) {
			if (isFirstCondition) {
				jpql += " where";
				isFirstCondition = false;
			} else {
				jpql += " and";
			}
			jpql += " o.status = :status";
		}
		//회원 이름 검색
		if (StringUtils.hasText(orderSearch.getMemberName())) {
			if (isFirstCondition) {
				jpql += " where";
				isFirstCondition = false;
			} else {
				jpql += " and";
			}
			jpql += " m.name like :name";
		}
		TypedQuery<Order> query = em.createQuery(jpql, Order.class)
			.setMaxResults(1000); //최대 1000건
		if (orderSearch.getOrderStatus() != null) {
			query = query.setParameter("status", orderSearch.getOrderStatus());
		}
		if (StringUtils.hasText(orderSearch.getMemberName())) {
			query = query.setParameter("name", orderSearch.getMemberName());
		}
		return query.getResultList();
	}

	public List<Order> findAllWithMemberDelivery() {
		return em.createQuery(
			"select o from Order o "
				+ "join fetch o.member m "
				+ "join fetch o.delivery", Order.class
		).getResultList();
	}

	public List<OrderSimpleQueryDto> findOrderDtos() {
		return em.createQuery(
			"select new jpabook.jpashop.repository.OrderSimpleQueryDto(o.id,m.name,o.)"
				+ " from Order o "
				+ "join o.member m "
				+ "join o.delivery d", OrderSimpleQueryDto.class)
			.getResultList();
	}

	public List<Order> findAllWithItem() {
		return em.createQuery(
			"select distinct o from Order o "
				+ "join fetch o.member m "
				+ "join fetch o.delivery d "
				+ "join fetch o.orderItems oi "
				+ "join fetch oi.item i", Order.class).getResultList();
	}

	public List<Order> findAllWithMemberDelivery(int offset, int limit) {
		return em.createQuery(
			"select o from Order o "
				+ "join fetch o.member m "
				+ "join fetch o.delivery", Order.class)
			.setFirstResult(offset)
			.setMaxResults(limit)
			.getResultList();
	}
}