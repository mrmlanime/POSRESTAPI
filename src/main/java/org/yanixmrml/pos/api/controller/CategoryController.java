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
import org.yanixmrml.pos.api.model.Category;
import org.yanixmrml.pos.api.service.CategoryService;
import org.yanixmrml.pos.api.service.ProductService;

import javassist.NotFoundException;

@RestController("CategoryController${vs}")
@RequestMapping("${url.category}")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ProductService productService;
	@Value("${url.category}")
	private String urlCategory;
	private Logger logger = LoggerFactory.getLogger(CategoryController.class);
	
	@GetMapping(produces= {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> getCategories(){
		//Implement the pagination and filter
		List<Category> categoryList = this.categoryService.getCategories();
		Link instanceLink = linkTo(methodOn(CategoryController.class).getCategories()).withRel("instance");
		instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.category}", urlCategory));
		HttpHeaders httpHeader = new HttpHeaders();
		httpHeader.setLocation(URI.create(instanceLink.getHref()));
		categoryList.stream().map(c->{
			try {
				Link selfLink = linkTo(methodOn(CategoryController.class).getCategory(c.getCategoryID())).withSelfRel();
				selfLink = selfLink.withHref(selfLink.getHref().replace("${url.category}", urlCategory));
				c.add(selfLink);
			}catch(Throwable e) {
				throw new RuntimeException(e.getLocalizedMessage());
			}
			return c;
		}).collect(Collectors.toList());
		logger.info("The category list was fetched");
		return new ResponseEntity<List<Category>>(categoryList,httpHeader,HttpStatus.OK);
	}
	
	@GetMapping(value="/{categoryID}",produces= {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> getCategory(@PathVariable("categoryID") int categoryID) throws Throwable{
		if(categoryID<=0) {
			throw new IllegalArgumentException(String.format("The categoryI %d is invalid.", categoryID));
		}else {
			Category category = this.categoryService.getCategory(categoryID);
			if(category==null) {
				throw new NotFoundException("The category details is not found.");
			}else {
				Link selfLink = linkTo(methodOn(CategoryController.class).getCategory(category.getCategoryID())).withRel("instance");
				selfLink = selfLink.withHref(selfLink.getHref().replace("${url.category}", urlCategory));
				HttpHeaders httpHeader = new HttpHeaders();
				HttpStatus status = HttpStatus.CREATED;
				String message = String.format("The category %s was found.", category);
				httpHeader.setLocation(URI.create(selfLink.getHref()));
				category.add(selfLink);
				logger.info(message);
				return new ResponseEntity<Category>(category,httpHeader,status);
			}
		}
	}
	
	@PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> addCategory(@RequestBody @Validated Category category)throws Throwable{
		if(category==null) {
			throw new IllegalArgumentException("The category details submitted is invalid.");
		}else {
			this.categoryService.addCategory(category);
			Link selfLink = linkTo(methodOn(CategoryController.class).getCategory(category.getCategoryID())).withRel("instance");
			selfLink = selfLink.withHref(selfLink.getHref().replace("${url.category}", urlCategory));
			HttpHeaders httpHeader = new HttpHeaders();
			HttpStatus status = HttpStatus.CREATED;
			String message = String.format("The category %s was created.", category);
			httpHeader.setLocation(URI.create(selfLink.getHref()));
			category.add(selfLink);
			ApiMessage<String> apiMessage = new ApiMessage<String>(LocalDateTime.now(),
					status,"Category Created",message,selfLink.getHref());
			return new ResponseEntity<ApiMessage<String>>(apiMessage,httpHeader,status);
		}
	}
	
	@PutMapping(value="/{categoryID}",produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> updateCategory(@PathVariable("categoryID") int categoryID,
			@RequestBody @Validated Category category) throws Throwable{
		if(categoryID<=0) {
			throw new IllegalArgumentException(String.format("The categoryID %d submitted is invalid", categoryID));
		}else if(category==null) {
			throw new IllegalArgumentException("The category details submitted is invalid");
		}else{
			category.setCategoryID(categoryID);
			this.categoryService.updateCategory(category);
			Link selfLink = linkTo(methodOn(CategoryController.class).getCategory(category.getCategoryID())).withRel("instance");
			selfLink = selfLink.withHref(selfLink.getHref().replace("${url.category}", urlCategory));
			HttpHeaders httpHeader = new HttpHeaders();
			HttpStatus status = HttpStatus.OK;
			String message = String.format("The category %s was updated", category);
			httpHeader.setLocation(URI.create(selfLink.getHref()));
			category.add(selfLink);
			logger.info(message);
			ApiMessage<String> apiMessage = new ApiMessage<String>(LocalDateTime.now(),
					status,"Category Updated",message,selfLink.getHref());
			return new ResponseEntity<ApiMessage<String>>(apiMessage,httpHeader,status);
		}
	}
	
	@DeleteMapping(value="/{categoryID}", produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> deleteCategory(@PathVariable("categoryID") int categoryID) throws Throwable{
		if(categoryID<=0) {
			throw new IllegalArgumentException(String.format("The categoryID: %d is invalid.", categoryID));
		}else {
			Link selfLink = linkTo(methodOn(CategoryController.class).deleteCategory(categoryID)).withSelfRel();
			selfLink = selfLink.withHref(selfLink.getHref().replace("${url.category}", urlCategory));
			HttpStatus status = HttpStatus.OK;
			HttpHeaders httpHeader = new HttpHeaders();
			String message;
			if(productService.countProductsByCategoryID(categoryID)>0) {
				this.categoryService.deactivateCategory(categoryID);
				message = String.format("The categoryID: %d was deactivated, category has associated products." ,categoryID);
			}else {
				this.categoryService.deleteCategory(categoryID);
				message = String.format("The categoryID: %d was deleted.", categoryID);
			}
			httpHeader.setLocation(URI.create(message));
			ApiMessage<String> apiMessage = new ApiMessage<String>(LocalDateTime.now(),
					status,"Category Deleted",message,selfLink.getHref());
			return new ResponseEntity<ApiMessage<String>>(apiMessage,httpHeader,status);
		}
	}
 	
	
	
}
