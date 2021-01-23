package kitchenpos.order.dto;

import java.util.List;
import java.util.stream.Collectors;

import kitchenpos.order.domain.OrderLineItem;

public class OrderLineItemResponse {
	private Long seq;
	private Long orderId;
	private Long menuId;
	private long quantity;

	protected OrderLineItemResponse() {
	}

	public OrderLineItemResponse(Long seq, Long orderId, Long menuId, long quantity) {
		this.seq = seq;
		this.orderId = orderId;
		this.menuId = menuId;
		this.quantity = quantity;
	}

	public Long getSeq() {
		return seq;
	}

	public Long getOrderId() {
		return orderId;
	}

	public Long getMenuId() {
		return menuId;
	}

	public long getQuantity() {
		return quantity;
	}

	public static OrderLineItemResponse of(OrderLineItem orderLineItem) {
		return new OrderLineItemResponse(orderLineItem.getSeq()
			, orderLineItem.getOrderId()
			, orderLineItem.getMenuId()
			, orderLineItem.getQuantity()
		);
	}

	public static List<OrderLineItemResponse> ofList(List<OrderLineItem> orderLineItems) {
		return orderLineItems.stream()
			.map(OrderLineItemResponse::of)
			.collect(Collectors.toList());
	}
}