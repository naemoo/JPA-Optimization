package jpabook.jpashop.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;

@SpringBootTest
@Transactional
class OrderServiceTest {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderRepository orderRepository;

	@Test
	public void 상품주문() throws Exception {
		//given
		Member member = createMember();
		em.persist(member);
		Book book = createBook(10000, 10, "시골 JPA");
		em.persist(book);

		int orderCount = 2;
		//when
		Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
		//then
		Order getOrder = orderRepository.findOne(orderId);
		Assertions.assertEquals(OrderStatus.ORDER, getOrder.getStatus());
		Assertions.assertEquals(1, getOrder.getOrderItems().size());
		Assertions.assertEquals(10000 * orderCount, getOrder.getTotalPrice());
		Assertions.assertEquals(8, book.getStockQuantity());
	}

	private Book createBook(int price, int stockQuantity, String name) {
		Book book = new Book();
		book.setName(name);
		book.setPrice(price);
		book.setStockQuantity(stockQuantity);
		return book;
	}

	private Member createMember() {
		Member member = new Member();
		member.setName("member1");
		member.setAddress(new Address("서울", "관천로", "123-456"));
		return member;
	}

	@Test
	public void 상품주문_재고초과() throws Exception {
		//given
		Member member = createMember();
		em.persist(member);
		Item item = createBook(10000, 10, "시골 JPA");
		em.persist(item);
		//when

		//then
		Assertions.assertThrows(NotEnoughStockException.class,
			() -> orderService.order(member.getId(), item.getId(), 11));
	}

	@Test
	public void 주문취소() throws Exception {
		//given
		Member member = createMember();
		Item item = createBook(10000, 10, "시골 JPA");
		em.persist(member);
		em.persist(item);
		int orderCount = 2;
		Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

		//when
		orderService.cancelOrder(orderId);

		//then
		Order getOrder = orderRepository.findOne(orderId);
		Assertions.assertEquals(OrderStatus.CANCLE, getOrder.getStatus());
		Assertions.assertEquals(10, item.getStockQuantity());
	}

}
