package org.yanixmrml.pos.api.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yanixmrml.pos.api.model.ApiMessage;
import org.yanixmrml.pos.api.model.ApiStatus;
import org.yanixmrml.pos.api.model.Order;
import org.yanixmrml.pos.api.service.OrderItemService;
import org.yanixmrml.pos.api.service.OrderService;

import javassist.NotFoundException;

@RestController("OrderController${vs}")
@RequestMapping("${url.order}")
public class OrderController {

	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderItemService orderItemService;
	@Value("${url.order}")
	private String orderURL;
	@Value("${url.store}")
	private String storeURL;
	@Value("${url.staff}")
	private String staffURL;
	private Logger logger = LoggerFactory.getLogger(OrderController.class);
	
	@GetMapping(produces={MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> getOrders() throws Throwable{
		//Implement a filtering/pagination here
		List<Order> orderList = this.orderService.getOrders();
		HttpHeaders httpHeader = new HttpHeaders();
		Link instanceLink = linkTo(methodOn(OrderController.class).getOrders()).withRel("instance");
		instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.order}",orderURL));
		httpHeader.setLocation(URI.create(instanceLink.getHref()));
		orderList.stream().map(o->{
			try {
				Link selfLink = linkTo(methodOn(OrderController.class).getOrder(o.getOrderID())).withSelfRel();
				selfLink = selfLink.withHref(selfLink.getHref().replace("${url.order}", orderURL));
				Link storeLink = linkTo(methodOn(StoreController.class).getStore(o.getStore().getStoreID())).withRel("store");
				storeLink = storeLink.withHref(storeLink.getHref().replace("${url.store}", storeURL));
				Link staffLink = linkTo(methodOn(StaffController.class).getStaff(o.getStaff().getStaffID())).withRel("staff");
				staffLink = staffLink.withHref(staffLink.getHref().replace("${url.staff}", staffURL));
				o.add(selfLink);
				o.add(storeLink);
				o.add(staffLink);
			}catch(Throwable e) {
				throw new RuntimeException(e.getLocalizedMessage());
			}
			return o;
		}).collect(Collectors.toList());
		logger.info("Fetching the list of oders.");
		return new ResponseEntity<List<Order>>(orderList,httpHeader,HttpStatus.OK);
	}
	
	@GetMapping(value="/{orderID}",produces={MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> getOrder(@PathVariable("orderID") int orderID) throws Throwable{
		if(orderID<=0) {
			throw new IllegalArgumentException(String.format("The orderID %d is invalid.", orderID));
		}else {
			Order order = this.orderService.getOrder(orderID);
			if(order==null) {
				throw new NotFoundException("No order record found.");
			}else {
				HttpHeaders httpHeader = new HttpHeaders();
				Link selfLink = linkTo(methodOn(OrderController.class).getOrder(order.getOrderID())).withSelfRel();
				selfLink = selfLink.withHref(selfLink.getHref().replace("${url.order}", orderURL));
				Link storeLink = linkTo(methodOn(StoreController.class).getStore(order.getStore().getStoreID())).withRel("store");
				storeLink = storeLink.withHref(storeLink.getHref().replace("${url.store}", storeURL));
				Link staffLink = linkTo(methodOn(StaffController.class).getStaff(order.getStaff().getStaffID())).withRel("staff");
				staffLink = staffLink.withHref(staffLink.getHref().replace("${url.staff}", staffURL));
				httpHeader.setLocation(URI.create(selfLink.getHref()));
				order.add(selfLink);
				order.add(storeLink);
				order.add(staffLink);
				httpHeader.setLocation(URI.create(selfLink.getHref()));
				logger.info(String.format("The order %s is found.", order));
				return new ResponseEntity<Order>(order,httpHeader,HttpStatus.OK);
			}
		}
	}
	
	@PostMapping(produces={MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> addOrder(@RequestBody @Validated Order order) throws Throwable{
		if(order==null) {
			throw new IllegalArgumentException("The order is invalid.");
		}else {
			this.orderService.addOrder(order);
			Link instanceLink = linkTo(methodOn(OrderController.class).addOrder(order)).withRel("instance");
			instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.order}", orderURL));
			Link selfLink = linkTo(methodOn(OrderController.class).getOrder(order.getOrderID())).withSelfRel();
			selfLink = selfLink.withHref(selfLink.getHref().replace("${url.order}", orderURL));
			Link storeLink = linkTo(methodOn(StoreController.class).getStore(order.getStore().getStoreID())).withRel("store");
			storeLink = storeLink.withHref(storeLink.getHref().replace("${url.store}", storeURL));
			Link staffLink = linkTo(methodOn(StaffController.class).getStaff(order.getStaff().getStaffID())).withRel("staff");
			staffLink = staffLink.withHref(staffLink.getHref().replace("${url.staff}", staffURL));
			HttpHeaders httpHeader = new HttpHeaders();
			HttpStatus status = HttpStatus.OK;
			httpHeader.setLocation(URI.create(instanceLink.getHref()));
			String message = String.format("The order %s was added.", order);
			logger.info(message);
			ApiMessage<Order> apiMessage = new ApiMessage<Order>(LocalDateTime.now(),
					status,message,order,instanceLink.getHref());
			return new ResponseEntity<ApiMessage<Order>>(apiMessage,httpHeader,status);
		}
	}
	
	@PutMapping(value="/{orderID}",produces={MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> updateOrder(@PathVariable("orderID") int orderID, @RequestBody @Validated Order order) throws Throwable {
		if(orderID<=0 || order==null) {
			throw new IllegalArgumentException("The orderID or order is not valid.");
		}else {
			order.setOrderID(orderID);
			this.orderService.updateOrder(order);
			Link instanceLink = linkTo(methodOn(OrderController.class).updateOrder(orderID,order)).withRel("instance");
			instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.order}", orderURL));
			Link selfLink = linkTo(methodOn(OrderController.class).getOrder(order.getOrderID())).withSelfRel();
			selfLink = selfLink.withHref(selfLink.getHref().replace("${url.order}", orderURL));
			Link storeLink = linkTo(methodOn(StoreController.class).getStore(order.getStore().getStoreID())).withRel("store");
			storeLink = storeLink.withHref(storeLink.getHref().replace("${url.store}", storeURL));
			Link staffLink = linkTo(methodOn(StaffController.class).getStaff(order.getStaff().getStaffID())).withRel("staff");
			staffLink = staffLink.withHref(staffLink.getHref().replace("${url.staff}", staffURL));
			HttpHeaders httpHeader = new HttpHeaders();
			HttpStatus status = HttpStatus.OK;
			httpHeader.setLocation(URI.create(instanceLink.getHref()));
			String message = String.format("The order %s was updated.", order);
			logger.info(message);
			ApiMessage<Order> apiMessage = new ApiMessage<Order>(LocalDateTime.now(),
					status,message,order,instanceLink.getHref());
			return new ResponseEntity<ApiMessage<Order>>(apiMessage,httpHeader,status);
		}
	}
	
	@DeleteMapping(value="/{orderID}",produces={MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> deleteOrder(@PathVariable("orderID") int orderID) throws Throwable{
		//check if order is paid and voided
		if(orderID<=0) {
			throw new IllegalArgumentException(String.format("The orderID %d is not valid.", orderID));
		}else {
			this.orderService.deleteOrder(orderID);
			String message;
			//has order items
			Order order = orderService.getOrder(orderID);
			if(order.getOrderStatus()==ApiStatus.ORDER_VOIDED) {
				this.orderItemService.deleteAllOrderItemsByOrderID(orderID);
				this.orderService.deleteOrder(orderID);
				message = String.format("The orderID %d was deleted.", orderID);
			}else {
				this.orderService.updateOrderStatus(ApiStatus.ORDER_VOIDED, orderID);
				message = String.format("The ordzerID %d was voided.", orderID);
			}
			Link instanceLink = linkTo(methodOn(OrderController.class).deleteOrder(orderID)).withRel("instance");
			instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.order}", orderURL));
			HttpHeaders httpHeader = new HttpHeaders();
			HttpStatus status = HttpStatus.OK;
			httpHeader.setLocation(URI.create(instanceLink.getHref()));
			logger.info(message);
			ApiMessage<String> apiMessage = new ApiMessage<String>(LocalDateTime.now(),
					status,message,String.format("orderID: %d", orderID),instanceLink.getHref());
			return new ResponseEntity<ApiMessage<String>>(apiMessage,httpHeader,status);
		}
	}
}
