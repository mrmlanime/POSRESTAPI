package org.yanixmrml.pos.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yanixmrml.pos.api.model.OrderItem;
import org.yanixmrml.pos.api.model.OrderItemID;
import org.yanixmrml.pos.api.repository.OrderItemRepository;

@Service
public class OrderItemService {

	@Autowired
	private OrderItemRepository orderItemRepository;
	
	public OrderItemService() {
		super();
	}
	
	public List<OrderItem> getOrderItems(int orderID){
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		//this.orderRepository.getOrderItemsByOrderID(orderID).forEach(orderItem -> orderItemList.add(orderItem));
		return orderItemList;
	}
	
	public OrderItem getOrderItem(OrderItemID orderItemID) {
		return this.orderItemRepository.findById(orderItemID).get();
	}
	
	public void addOrderItem(OrderItem orderItem) {
		this.orderItemRepository.save(orderItem);
	}
	
	public void updateOrderItem(OrderItem orderItem) {
		this.orderItemRepository.save(orderItem);
	}
	
	public void deleteOrderItem(OrderItemID orderItemID) {
		this.orderItemRepository.deleteById(orderItemID);
	}
	
	public void deleteAllOrderItemsByOrderID(int orderID) {
		this.orderItemRepository.deleteAllOrderItemsByOrderID(orderID);
	}
	
	public int countOrderItemsByProductID(int productID) {
		return this.orderItemRepository.countOrderItemsByProductID(productID);
	}
	
}
