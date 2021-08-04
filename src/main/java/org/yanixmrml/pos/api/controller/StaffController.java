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
import org.yanixmrml.pos.api.model.Staff;
import org.yanixmrml.pos.api.service.OrderService;
import org.yanixmrml.pos.api.service.StaffService;

import javassist.NotFoundException;

@RestController("StaffController${vs}")
@RequestMapping("${url.staff}")
public class StaffController {

	@Autowired
	private StaffService staffService;
	@Autowired
	private OrderService orderService;
	@Value("${url.staff}")
	private String urlStaff;
	@Value("${url.store}")
	private String urlStore;
	private Logger logger = LoggerFactory.getLogger(StaffController.class);
	
	@GetMapping(produces= {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> getStaffs() throws Throwable{
		//Implement pagination here & filter
		List<Staff> staffList = this.staffService.getStaffs();
		Link instanceLink = linkTo(methodOn(StaffController.class).getStaffs()).withSelfRel();
		instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.staff}", urlStaff));
		HttpHeaders httpHeader = new HttpHeaders();
		httpHeader.setLocation(URI.create(instanceLink.getHref()));
		staffList.stream().map(s->{
			try {
				Link selfLink = linkTo(methodOn(StaffController.class).getStaff(s.getStaffID())).withSelfRel();
				selfLink = selfLink.withHref(selfLink.getHref().replaceAll("${url.staff}", urlStaff));
				Link storeLink = linkTo(methodOn(StoreController.class).getStore(s.getStore().getStoreID())).withRel("store");
				storeLink = storeLink.withHref(storeLink.getHref().replace("${url.store}", urlStore));
				s.add(selfLink);
				s.add(storeLink);
			}catch(Throwable e) {
				throw new RuntimeException("Error in adding link.");
			}
			return s;
		}).collect(Collectors.toList());
		logger.info("The staffList was fetched.");
		return new ResponseEntity<List<Staff>>(staffList,httpHeader,HttpStatus.OK);
	}
	
	@GetMapping(value="/{staffID}",produces= {MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> getStaff(@PathVariable("staffID") int staffID) throws Throwable {
		if(staffID<=0) {
			throw new IllegalArgumentException(String.format("The staffID %d is not valid.", staffID));
		}else {
			Staff staff = this.staffService.getStaff(staffID);
			if(staff!=null) {
				Link selfLink = linkTo(methodOn(StaffController.class).getStaff(staffID)).withSelfRel();
				selfLink = selfLink.withHref(selfLink.getHref().replace("${url.staff}", urlStaff));
				Link storeLink = linkTo(methodOn(StoreController.class).getStore(staff.getStore().getStoreID())).withRel("store");
				storeLink = storeLink.withHref(storeLink.getHref().replace("${url.store}", urlStore));
				HttpHeaders httpHeader = new HttpHeaders();
				httpHeader.setLocation(URI.create(selfLink.getHref()));
				staff.add(selfLink);
				staff.add(storeLink);
				logger.info(String.format("The staff %s was found",staff));
				return new ResponseEntity<Staff>(staff,httpHeader,HttpStatus.OK);
			}else {
				throw new NotFoundException(String.format("The staff, staffID: %d is not found.", staffID));
			}
		}
	}
	
	@PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> addStaff(@RequestBody @Validated Staff staff) throws Throwable{
		if(staff==null) {
			throw new IllegalArgumentException("The staff is invalid");
		}else {
			this.staffService.addStaff(staff);
			Link instanceLink = linkTo(methodOn(StaffController.class).addStaff(staff)).withRel("instance");
			instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.staff}", urlStaff));
			Link selfLink = linkTo(methodOn(StaffController.class).getStaff(staff.getStaffID())).withSelfRel();
			selfLink = selfLink.withHref(selfLink.getHref().replace("${url.staff}", urlStore));
			Link storeLink = linkTo(methodOn(StoreController.class).getStore(staff.getStore().getStoreID())).withRel("store");
			storeLink = storeLink.withHref(storeLink.getHref().replace("${url.store}", urlStore));
			HttpHeaders httpHeader = new HttpHeaders();
			HttpStatus status = HttpStatus.OK;
			httpHeader.setLocation(URI.create(instanceLink.getHref()));
			staff.add(selfLink);
			staff.add(storeLink);
			logger.info(String.format("The staff %s was added",staff));
			ApiMessage<Staff> apiMessage = new ApiMessage<Staff>(LocalDateTime.now(),
					status,"Staff was added.",staff,selfLink.getHref());
			return new ResponseEntity<ApiMessage<Staff>>(apiMessage,httpHeader,status);
		}
	}
	
	@PutMapping(value="/{staffID}",produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> updateStaff(@PathVariable("staffID") int staffID, 
			@RequestBody @Validated Staff staff) throws Throwable {
		if(staffID<=0) {
			throw new IllegalArgumentException(String.format("The staffID %d is invalid.", staffID));
		}else if(staff==null) {
			throw new NullPointerException("The staff details in invalid.");
		}else {
			//Note make sure that info has the old value from the previous client may pass partial value hence only use patch
			staff.setStaffID(staffID);
			this.staffService.updateStaff(staff);
			Link selfLink = linkTo(methodOn(StaffController.class).getStaff(staff.getStaffID())).withSelfRel();
			selfLink = selfLink.withHref(selfLink.getHref().replace("${url.staff}", urlStaff));
			Link storeLink = linkTo(methodOn(StoreController.class).getStore(staff.getStore().getStoreID())).withRel("store");
			storeLink = storeLink.withHref(storeLink.getHref().replace("${url.store}", urlStore));
			HttpHeaders httpHeader = new HttpHeaders();
			HttpStatus status = HttpStatus.OK;
			httpHeader.setLocation(URI.create(selfLink.getHref()));
			staff.add(selfLink);
			staff.add(storeLink);
			logger.info(String.format("The staff %s was updated",staff));
			ApiMessage<Staff> apiMessage = new ApiMessage<Staff>(LocalDateTime.now(),
					status,"Staff was updated.",staff,selfLink.getHref());
			return new ResponseEntity<ApiMessage<Staff>>(apiMessage,httpHeader,status);
		}
	}

	@DeleteMapping(value="/{staffID}",produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> deleteStaff(@PathVariable("staffID") int staffID) throws Throwable {
		if(staffID<=0) {
			throw new IllegalArgumentException(String.format("The staffID %d is not valid", staffID));
		}else {
			//check if there is associated order form staff, if not do not delete
			if(orderService.countOrdersByStaffID(staffID)>0) {
				throw new RuntimeException(String.format("The staffID: %d has associated orders, cannot be deleted.", staffID));
			}else{
				this.staffService.deleteStaff(staffID);
				Link instanceLink = linkTo(methodOn(StaffController.class).deleteStaff(staffID)).withRel("Instance");
				instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.staff}", urlStaff));
				HttpHeaders httpHeader = new HttpHeaders();
				HttpStatus status = HttpStatus.OK;
				httpHeader.setLocation(URI.create(instanceLink.getHref()));
				String message = String.format("The staffID: %d was deleted.", staffID);
				logger.info(message);
				ApiMessage<String> apiMessage = new ApiMessage<String>(LocalDateTime.now(),
						status,message, String.format("staffID : %d", staffID),instanceLink.getHref());
				return new ResponseEntity<ApiMessage<String>>(apiMessage,httpHeader,status);
			}
		}
	}
	
	
}
