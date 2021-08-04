package org.yanixmrml.pos.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yanixmrml.pos.api.model.Order;
import org.yanixmrml.pos.api.repository.OrderRepository;

@Service
public class OrderService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	public OrderService() {
		super();
	}
	
	public List<Order> getOrders(){
		List<Order> orderList = new ArrayList<Order>();
		this.orderRepository.findAll().forEach(order -> orderList.add(order));
		return orderList;
	}
	
	public Order getOrder(int orderID) {
		return this.orderRepository.findById(orderID).get();
	}
	
	public void addOrder(Order order) {
		this.orderRepository.save(order);
	}
	
	public void updateOrder(Order order) {
		this.orderRepository.save(order);
	}
	
	public void deleteOrder(int orderID) {
		this.orderRepository.deleteById(orderID);
	}
	
	public int countOrdersByCustomerID(int customerID) {
		return this.orderRepository.countOrdersByCustomerID(customerID);
	}
	
	public int countOrdersByStaffID(int staffID) {
		return this.orderRepository.countOrdersByStaffID(staffID);
	}
	
	public void updateOrderStatus(int orderStatus,int orderID) {
		this.orderRepository.updateOrderStatus(orderStatus, orderID);
	}
}
