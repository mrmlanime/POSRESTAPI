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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.yanixmrml.pos.api.model.ApiMessage;
import org.yanixmrml.pos.api.model.Order;
import org.yanixmrml.pos.api.model.OrderItem;
import org.yanixmrml.pos.api.model.OrderItemID;
import org.yanixmrml.pos.api.service.OrderItemService;
import org.yanixmrml.pos.api.service.OrderService;

import javassist.NotFoundException;

@RestController("OrderItemController${vs}")
@RequestMapping("${url.order.item}")
public class OrderItemController {

	@Autowired
	private OrderItemService orderItemService;
	@Autowired
	private OrderService orderService;
	@Value("${url.order.item}")
	private String orderItemURL;
	private Logger logger = LoggerFactory.getLogger(OrderItemController.class);
	
	@GetMapping(produces={MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> getOrderItems(@PathVariable("orderID") int orderID) throws Throwable{
		if(orderID<=0) {
			throw new IllegalArgumentException(String.format("The orderID %d is invalid.",orderID));
		}else {
			List<OrderItem> orderItemList = this.orderItemService.getOrderItems(orderID);
			Link instanceLink = linkTo(methodOn(OrderItemController.class).getOrderItems(orderID)).withRel("instance");
			instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.order.item}", orderItemURL));
			HttpHeaders httpHeader = new HttpHeaders();
			httpHeader.setLocation(URI.create(instanceLink.getHref()));
			orderItemList.stream().map(o->{
				try {
					Link selfLink = linkTo(methodOn(OrderItemController.class).getOrderItem(orderID, o.getProduct().getProductID())).withSelfRel();
					selfLink = selfLink.withHref(selfLink.getHref().replace("${url.order.item}", orderItemURL));
					Link productLink = linkTo(methodOn(ProductController.class).getProduct(o.getProduct().getProductID())).withRel("product");
					productLink = productLink.withHref(productLink.getHref().replace("${url.order.item}", orderItemURL));
					o.add(selfLink);
					o.add(productLink);
				}catch(Throwable e) {
					return new RuntimeException(e.getLocalizedMessage());
				}
				return o;
			}).collect(Collectors.toList());
			logger.info("The orderitems are fetched.");
			return new ResponseEntity<List<OrderItem>>(orderItemList,httpHeader,HttpStatus.OK);
		}
	}
	
	@GetMapping(value="/{productID}",produces={MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> getOrderItem(@PathVariable("orderID") int orderID, 
			@PathVariable("productID") int productID) throws Throwable{
		if(orderID<=0 || productID<=0) {
			throw new IllegalArgumentException("The orderID or productID is invalid");
		}else{
			OrderItem orderItem = this.orderItemService.getOrderItem(new OrderItemID(orderID,productID));	
			if(orderItem==null) {
				throw new NullPointerException("No orderItem was found.");
			}else {
				Link selfLink = linkTo(methodOn(OrderItemController.class).getOrderItem(orderID, orderItem.getProduct().getProductID())).withSelfRel();
				selfLink = selfLink.withHref(selfLink.getHref().replace("${url.order.item}", orderItemURL));
				Link productLink = linkTo(methodOn(ProductController.class).getProduct(orderItem.getProduct().getProductID())).withRel("product");
				productLink = productLink.withHref(productLink.getHref().replace("${url.order.item}", orderItemURL));
				orderItem.add(selfLink);
				orderItem.add(productLink);
				HttpHeaders httpHeader = new HttpHeaders();
				httpHeader.setLocation(URI.create(selfLink.getHref()));
				logger.info("The orderitem was created.");
				return new ResponseEntity<OrderItem>(orderItem,httpHeader,HttpStatus.CREATED);
			}
		}
	}
	
	@PostMapping(produces={MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> addOrderItem(@PathVariable("orderID") int orderID,
			@RequestBody @Validated OrderItem orderItem) throws Throwable {
		if(orderItem!=null) {
			Order order = orderService.getOrder(orderID);
			if(order!=null) {
				this.orderItemService.addOrderItem(orderItem);
				Link instanceLink = linkTo(methodOn(OrderItemController.class).addOrderItem(orderID, orderItem)).withRel("instance");
				instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.order.item}", orderItemURL));
				Link selfLink = linkTo(methodOn(OrderItemController.class).getOrderItem(orderID, orderItem.getProduct().getProductID())).withSelfRel();
				selfLink = selfLink.withHref(selfLink.getHref().replace("${url.order.item}", orderItemURL));
				Link productLink = linkTo(methodOn(ProductController.class).getProduct(orderItem.getProduct().getProductID())).withRel("product");
				productLink = productLink.withHref(productLink.getHref().replace("${url.order.item}", orderItemURL));
				orderItem.add(selfLink);
				orderItem.add(productLink);
				HttpHeaders httpHeader = new HttpHeaders();
				HttpStatus status = HttpStatus.CREATED;
				String message = String.format("The orderItem was added under orderID: %d", orderID);
				httpHeader.setLocation(URI.create(instanceLink.getHref()));
				logger.info(message);
				ApiMessage<OrderItem> apiMessage = new ApiMessage<OrderItem>(LocalDateTime.now(),
						status,message,orderItem,instanceLink.getHref());
				return new ResponseEntity<ApiMessage<OrderItem>>(apiMessage,httpHeader,status);
			}else {
				throw new NotFoundException(String.format("The orderID %d is not found", orderID));
			}
		}else{
			throw new IllegalArgumentException("The orderItem is invalid.");
		}
	}
	
	@RequestMapping(value="/{productID}",method=RequestMethod.PUT)
	public ResponseEntity<?> updateOrderItem(@PathVariable("orderID") int orderID, @PathVariable("productID") int productID,
			@RequestBody @Validated OrderItem orderItem) throws Throwable {
		if(orderID<=0 || productID<=0) {
			throw new IllegalArgumentException("The orderID or productID is invalid");
		}else if(orderItem==null) {
			throw new NullPointerException("The orderItem details is invalid");
		}else{
			orderItem.setOrderItemID(new OrderItemID(orderID,productID));
			this.orderItemService.updateOrderItem(orderItem);
			Link instanceLink = linkTo(methodOn(OrderItemController.class).updateOrderItem(orderID, productID,orderItem)).withRel("instance");
			instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.order.item}", orderItemURL));
			HttpHeaders httpHeader = new HttpHeaders();
			HttpStatus status = HttpStatus.OK;
			String message = String.format("The orderItem OrderID: %d, ProductID: %d was updated.",orderID,productID);
			httpHeader.setLocation(URI.create(instanceLink.getHref()));
			logger.info(message);
			ApiMessage<String> apiMessage = new ApiMessage<String>(LocalDateTime.now(),
					status,message,String.format("OrderID: %d, ProductID: %d", orderID,productID),instanceLink.getHref());
			return new ResponseEntity<ApiMessage<String>>(apiMessage,httpHeader,status);
		}
	}
	
	@RequestMapping(value="/{orderID}/{productID}",method=RequestMethod.DELETE)
	public ResponseEntity<?> deleteOrderItem(@PathVariable("orderID") int orderID, 
			@PathVariable("productID") int productID) throws Throwable {
		if(orderID<=0 || productID<=0) {
			throw new IllegalArgumentException("The orderID or productID is invalid");
		}else{
			this.orderItemService.deleteOrderItem(new OrderItemID(orderID,productID));
			Link instanceLink = linkTo(methodOn(OrderItemController.class).deleteOrderItem(orderID, productID)).withRel("instance");
			instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.order.item}", orderItemURL));
			HttpHeaders httpHeader = new HttpHeaders();
			HttpStatus status = HttpStatus.OK;
			String message = String.format("The orderItem OrderID: %d, ProductID: %d was deleted.",orderID,productID);
			httpHeader.setLocation(URI.create(instanceLink.getHref()));
			logger.info(message);
			ApiMessage<String> apiMessage = new ApiMessage<String>(LocalDateTime.now(),
					status,message,String.format("OrderID: %d, ProductID: %d", orderID,productID),instanceLink.getHref());
			return new ResponseEntity<ApiMessage<String>>(apiMessage,httpHeader,status);
		}	
	}
	
	
}
