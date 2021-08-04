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
import org.yanixmrml.pos.api.model.Store;
import org.yanixmrml.pos.api.service.StoreService;

import javassist.NotFoundException;

@RestController("StoreController${vs}")
@RequestMapping("${url.store}")
public class StoreController {

	@Autowired
	private StoreService storeService;
	@Value("${url.store}")
	private String urlStore;
	private Logger logger = LoggerFactory.getLogger(StoreController.class);
	
	@GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> getStores() throws Throwable{
		//Implement pagination and filtering here
		List<Store> storeList =  storeService.getStores();
		Link instanceLink = linkTo(methodOn(StoreController.class).getStores()).withRel("instance");
		instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.store}", urlStore));
		HttpHeaders httpHeader = new HttpHeaders();
		httpHeader.setLocation(URI.create(instanceLink.getHref()));
		storeList.stream().map(s->{
			try {
				Link selfLink = linkTo(methodOn(StoreController.class).getStore(s.getStoreID())).withSelfRel();
				selfLink = selfLink.withHref(selfLink.getHref().replace("${url.store}", urlStore));
				s.add(selfLink);
			}catch(Throwable e){
				throw new RuntimeException(e.getLocalizedMessage());
			}
			return s;
		}).collect(Collectors.toList());
		logger.info("The store list wast fetched.");
		return new ResponseEntity<List<Store>>(storeList,httpHeader,HttpStatus.OK);
	}
	
	@GetMapping(value="/{storeID}",produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> getStore(@PathVariable("storeID") int storeID) throws Throwable{
		if(storeID<=0) {
			throw new IllegalArgumentException(String.format("The storeID %d is invalid.", storeID));
		}else {
			Store store = storeService.getStore(storeID);
			if(store!=null) {
				Link selfLink = linkTo(methodOn(StoreController.class).getStore(store.getStoreID())).withSelfRel();
				selfLink = selfLink.withHref(selfLink.getHref().replace("${url.store}", urlStore));
				HttpHeaders httpHeader = new HttpHeaders();
				store.add(selfLink);
				httpHeader.setLocation(URI.create(selfLink.getHref()));
				return new ResponseEntity<Store>(store,httpHeader,HttpStatus.OK);
			}else {
				throw new NotFoundException("The store details is not found.");
			}
		}
	}
	
	@PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> addStore(@RequestBody @Validated Store store) throws Throwable {
		if(store==null) {
			throw new IllegalArgumentException("The store details passed is invalid.");
		}else {
			this.storeService.addStore(store);
			Link instanceLink = linkTo(methodOn(StoreController.class).addStore(store)).withRel("instance");
			instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.store}", urlStore));
			Link selfLink = linkTo(methodOn(StoreController.class).getStore(store.getStoreID())).withSelfRel();
			selfLink = selfLink.withHref(selfLink.getHref().replace("${url.store}", urlStore));
			HttpHeaders httpHeader = new HttpHeaders();
			HttpStatus status = HttpStatus.CREATED;
			String message = String.format("The store %s was added.", store);
			httpHeader.setLocation(URI.create(instanceLink.getHref()));
			store.add(selfLink);
			ApiMessage<String> apiMessage = new ApiMessage<String>(LocalDateTime.now(),
					status,"Store Created",message,selfLink.getHref());
			return new ResponseEntity<ApiMessage<String>>(apiMessage,httpHeader,status);
		}
	}
	
	@PutMapping(value="/{storeID}",produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> updateStore(@PathVariable("storeID") int storeID, @RequestBody @Validated Store store) {
		if(storeID<=0) {
			throw new IllegalArgumentException(String.format("The storeID: %d is not valid",storeID));
		}else if(store==null) {
			throw new NullPointerException("The store details passed is invalid");
		}else {
			store.setStoreID(storeID);
			this.storeService.updateStore(store);
			Link instanceLink = linkTo(methodOn(StoreController.class).updateStore(storeID,store)).withRel("instance");
			instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.store}", urlStore));
			HttpHeaders httpHeader = new HttpHeaders();
			HttpStatus status = HttpStatus.CREATED;
			String message = String.format("The store %s was updated.", store);
			httpHeader.setLocation(URI.create(instanceLink.getHref()));
			ApiMessage<String> apiMessage = new ApiMessage<String>(LocalDateTime.now(),
					status,"Store Updated",message,instanceLink.getHref());
			return new ResponseEntity<ApiMessage<String>>(apiMessage,httpHeader,status);
		}	
	}
	
	@DeleteMapping(value="/{storeID}",produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> deleteStore(@PathVariable("storeID") int storeID) throws Throwable {
		if(storeID<=0) {
			throw new IllegalArgumentException(String.format("The storeID: %d is not valid.", storeID));
		}else{
			//Check if the store has associated staff	
			if(!storeService.canBeDeleted(storeID)) {
				throw new RuntimeException(String.format("This storeID: %d cannot be deleted, has associated orders and staff",storeID));
			}else {
				this.storeService.deleteStore(storeID);
				Link instanceLink = linkTo(methodOn(StoreController.class).deleteStore(storeID)).withRel("instance");
				instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.store}", urlStore));
				HttpHeaders httpHeader = new HttpHeaders();
				HttpStatus status = HttpStatus.OK;
				String message = String.format("The store %d was deleted.", storeID);
				httpHeader.setLocation(URI.create(instanceLink.getHref()));
				ApiMessage<String> apiMessage = new ApiMessage<String>(LocalDateTime.now(),
						status,"Store Deleted",message,instanceLink.getHref());
				return new ResponseEntity<ApiMessage<String>>(apiMessage,httpHeader,status);
			}
		}
	}
	
}
