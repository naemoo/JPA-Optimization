package jpabook.jpashop.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
	@PersistenceContext
	private final EntityManager em;

	public void save(Item item) {
		if (item.getId() == null) {
			em.persist(item);
		} else {
			em.merge(item);
		}
	}

	public Item findOne(Long id) {
		Item item = em.find(Item.class, id);
		return item;
	}

	public List<Item> findAll() {
		List<Item> resultItems = em.createQuery("select i from Item i", Item.class).getResultList();
		return resultItems;
	}
}
