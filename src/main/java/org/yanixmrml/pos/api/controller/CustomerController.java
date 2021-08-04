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
import org.yanixmrml.pos.api.model.Customer;
import org.yanixmrml.pos.api.service.CustomerService;
import org.yanixmrml.pos.api.service.OrderService;

@RestController("CustomerController${vs}")
@RequestMapping("${url.customer}")
public class CustomerController {

	@Autowired
	private CustomerService customerService;
	@Autowired
	private OrderService orderService;
	@Value("${url.customer}")
	private String customerURL;
	private Logger logger = LoggerFactory.getLogger(CustomerController.class);
	
	@GetMapping(produces={MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> getCustomers() throws Throwable{	
		//Default pagination here and custom filter or sort here
		List<Customer> customerList = this.customerService.getCustomers();
		HttpHeaders httpHeader = new HttpHeaders();
		HttpStatus status = HttpStatus.OK;
		Link instanceLink = linkTo(methodOn(CustomerController.class).getCustomers()).withRel("instance");
		instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.customer}", customerURL));
		httpHeader.setLocation(URI.create(instanceLink.getHref()));	
		customerList.stream().map(c->{
			try {
				Link selfLink = linkTo(methodOn(CustomerController.class).getCustomer(c.getCustomerID())).withSelfRel();
				selfLink = selfLink.withHref(selfLink.getHref().replace("${url.customer}", customerURL));
				c.add(selfLink);
			}catch(Throwable e) {
				throw new RuntimeException("There is problem with adding link.");
			}
			return c;
		}).collect(Collectors.toList());
		return new ResponseEntity<List<Customer>>(customerList,httpHeader,status);
	}
	
	@GetMapping(value="/{customerID}",produces={MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> getCustomer(@PathVariable("customerID") int customerID) throws Throwable {
		if(customerID<=0) {
			throw new IllegalArgumentException(String.format("The customerID %d is invalid.",customerID));
		}else {
			Customer customer = this.customerService.getCustomer(customerID);
			Link selfLink = linkTo(methodOn(CustomerController.class).getCustomer(customerID)).withSelfRel();
			selfLink = selfLink.withHref(selfLink.getHref().replace("${url.customer}",customerURL));
			HttpHeaders httpHeader = new HttpHeaders();
			httpHeader.setLocation(URI.create(selfLink.getHref()));
			customer.add(selfLink);
			logger.info(String.format("The customer %s was found.",customer));
			return new ResponseEntity<Customer>(customer,httpHeader,HttpStatus.OK);
		}
	}
	
	@PostMapping(produces={MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> addCustomer(@RequestBody @Validated Customer customer) throws Throwable{
		if(customer==null) {
			throw new NullPointerException(String.format("The customer %s is invalid.",customer));
		}else {
			this.customerService.addCustomer(customer);
			Link instanceLink = linkTo(methodOn(CustomerController.class).addCustomer(customer)).withRel("instance");
			instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.customer}", customerURL));
			Link selfLink = linkTo(methodOn(CustomerController.class).getCustomer(customer.getCustomerID())).withSelfRel();
			selfLink = selfLink.withHref(selfLink.getHref().replace("${url.customer}", customerURL));
			HttpHeaders httpHeader = new HttpHeaders();
			HttpStatus status = HttpStatus.OK;
			String message = String.format("The customer %s was added", customer);
			httpHeader.setLocation(URI.create(instanceLink.getHref()));
			customer.add(selfLink);
			logger.info(message);
			ApiMessage<Customer> apiMessage = new ApiMessage<Customer>(LocalDateTime.now(),
					status,message,customer,instanceLink.getHref());	
			return new ResponseEntity<ApiMessage<Customer>>(apiMessage,httpHeader,status);
		}
	}
	
	@PutMapping(value="/{customerID}",produces={MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> updateCustomer(@PathVariable("customerID") int customerID, 
			@RequestBody @Validated Customer customer) throws Throwable {
		if(customerID<=0||customer==null) {
			throw new IllegalArgumentException("The customerID or customer is invalid");
		}else {
			customer.setCustomerID(customerID);
			this.customerService.updateCustomer(customer);
			Link selfLink = linkTo(methodOn(CustomerController.class).getCustomer(customerID)).withSelfRel();
			HttpHeaders httpHeader = new HttpHeaders();
			HttpStatus status = HttpStatus.OK;
			String message = String.format("The customer %s was updated.", customer);
			selfLink = selfLink.withHref(selfLink.getHref().replace("${url.customer}", customerURL));
			httpHeader.setLocation(URI.create(selfLink.getHref()));
			customer.add(selfLink);
			logger.info(message);
			ApiMessage<Customer> apiMessage = new ApiMessage<Customer>(LocalDateTime.now(),
					status,message,customer,selfLink.getHref());
			return new ResponseEntity<ApiMessage<Customer>>(apiMessage,httpHeader,status);
		}	
	}
	
	@DeleteMapping(value="/{customerID}",produces={MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> deleteCustomer(@PathVariable("customerID") int customerID) throws Throwable{
		if(customerID<=0) {
			throw new IllegalArgumentException(String.format("The customerID %d is invalid",customerID));
		}else {
			//has order
			String message;
			if(this.orderService.countOrdersByCustomerID(customerID)>0) {
				this.customerService.deactivateCustomer(customerID);
				message = String.format("The customerID %d was deactivated.", customerID);
			}else {
				this.customerService.deleteCustomer(customerID);
				message = String.format("The customerID %d was deleted.", customerID);
			}
			Link selfLink = linkTo(methodOn(CustomerController.class).deleteCustomer(customerID)).withRel("instance");
			selfLink = selfLink.withHref(selfLink.getHref().replace("${url.customer}", customerURL));
			HttpHeaders httpHeader = new HttpHeaders();
			HttpStatus status = HttpStatus.OK;
			httpHeader.setLocation(URI.create(selfLink.getHref()));
			logger.info(message);
			ApiMessage<String> apiMessage = new ApiMessage<String>(LocalDateTime.now(),
					status,message,String.format("customerID: %d", customerID),selfLink.getHref());
			return new ResponseEntity<ApiMessage<String>>(apiMessage,httpHeader,status);
		}
	}
}
