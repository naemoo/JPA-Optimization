package jpabook.jpashop.repository.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class OrderItemQueryDto {
	private Long orderId;
	private String itemName;
	private int orderPrice;
	private int count;

	@QueryProjection
	public OrderItemQueryDto(Long orderId, String itemName, int orderPrice, int count) {
		this.orderId = orderId;
		this.itemName = itemName;
		this.orderPrice = orderPrice;
		this.count = count;
	}
}
