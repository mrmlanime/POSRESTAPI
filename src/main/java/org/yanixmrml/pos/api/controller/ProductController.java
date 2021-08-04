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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.yanixmrml.pos.api.model.ApiMessage;
import org.yanixmrml.pos.api.model.Product;
import org.yanixmrml.pos.api.service.OrderItemService;
import org.yanixmrml.pos.api.service.ProductService;

import javassist.NotFoundException;

@RestController("ProductController${vs}")
@RequestMapping("${url.product}")
public class ProductController {

	@Autowired
	private ProductService productService;
	@Autowired
	private OrderItemService orderItemService;
	@Value("${url.product}")
	private String urlProduct;
	@Value("${url.brand}")
	private String urlBrand;
	private Logger logger = LoggerFactory.getLogger(ProductController.class);
	
	@GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> getProductsByName(@RequestParam("name") String name, 
			@RequestParam("categoryID") int categoryID) throws Throwable{
		List<Product> productList;
		Link selfLink = linkTo(methodOn(ProductController.class).getProductsByName(name,categoryID)).withSelfRel();
		selfLink = selfLink.withHref(selfLink.getHref().replace("${url.product}", urlProduct));
		HttpHeaders httpHeader = new HttpHeaders();
		httpHeader.setLocation(URI.create(selfLink.getHref()));
		if(name!=null && categoryID >= 0) {
			if(categoryID==0) {
				productList = this.productService.getProductsByName(name);
			}else {
				productList = this.productService.getProductsByCategoryAndName(categoryID, name);
			}
		}else if(categoryID>0){
			productList =  this.productService.getProductsByCategory(categoryID);
		}else {
			productList = this.productService.getProducts();
		}
		productList.stream().map(p->{
			try {
				Link sLink = linkTo(methodOn(ProductController.class).getProduct(p.getProductID())).withSelfRel();
				sLink = sLink.withHref(sLink.getHref().replace("${url.product}", urlProduct));
				Link bLink = linkTo(methodOn(BrandController.class).getBrand(p.getBrand().getBrandID())).withRel("brand");
				bLink = bLink.withHref(bLink.getHref().replace("${url.brand}", urlBrand));
				//Link cLink = linkTo(methodOn(CategoryController.class).get)
				p.add(sLink);
				p.add(bLink);
			}catch(Throwable e) {
				throw new RuntimeException("Problem with the adding of Link. " + e.getLocalizedMessage());
			}
			return p;
		}).collect(Collectors.toList());
		return new ResponseEntity<List<Product>>(productList,httpHeader,HttpStatus.OK);
	}
	
	@GetMapping(value="/{productID}",produces= {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> getProduct(@PathVariable("productID") int productID) throws Throwable {
		if(productID<=0) {
			throw new IllegalArgumentException("The productID is invalid.");
		}else {
			Product product = this.productService.getProduct(productID);
			if(product!=null) {
				Link selfLink = linkTo(methodOn(ProductController.class).getProduct(productID)).withSelfRel();
				selfLink = selfLink.withHref(selfLink.getHref().replace("${url.product}", urlProduct));
				Link brandLink = linkTo(methodOn(BrandController.class).getBrand(product.getBrand().getBrandID())).withRel("brand");
				brandLink = brandLink.withHref(brandLink.getHref().replace("${url.brand}", urlBrand));
				HttpHeaders httpHeader = new HttpHeaders();
				httpHeader.setLocation(URI.create(selfLink.getHref()));
				product.add(selfLink);
				product.add(brandLink);
				logger.info(String.format("The product %s was found",product));
				return new ResponseEntity<Product>(product,httpHeader,HttpStatus.OK);
			}else {
				throw new NotFoundException(String.format("The product,productID: %d is not found.", productID));
			}
		}
	}

	@PostMapping(produces= {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> addProduct(@RequestBody @Validated Product product) throws Throwable {
		if(product==null) {
			throw new NullPointerException("The product is null.");
		}else {
			this.productService.addProduct(product);
			Link instanceLink = linkTo(methodOn(ProductController.class).addProduct(product)).withRel("instance");
			instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.product}", urlProduct));
			Link selfLink = linkTo(methodOn(ProductController.class).getProduct(product.getProductID())).withSelfRel();
			selfLink = selfLink.withHref(selfLink.getHref().replace("${url.product}", urlProduct));
			Link brandLink = linkTo(methodOn(BrandController.class).getBrand(product.getBrand().getBrandID())).withRel("brand");
			brandLink = brandLink.withHref(brandLink.getHref().replace("${url.brand}", urlBrand));
			HttpHeaders httpHeader = new HttpHeaders();
			HttpStatus status = HttpStatus.OK;
			httpHeader.setLocation(URI.create(instanceLink.getHref()));
			product.add(selfLink);
			product.add(brandLink);
			logger.info(String.format("The product %s was added",product));
			ApiMessage<Product> apiMessage = new ApiMessage<Product>(LocalDateTime.now(),
					status,"Product was added.",product,selfLink.getHref());
			return new ResponseEntity<ApiMessage<Product>>(apiMessage,httpHeader,status);
		}
	}
	
	@PutMapping(value="/{productID}",produces= {MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> updateProduct(@PathVariable("productID") int productID, 
			@RequestBody @Validated Product product) throws Throwable {
		if(productID<=0 || product==null) {
			throw new IllegalArgumentException("The productID or product is not valid.");
		}else {
			product.setProductID(productID);
			this.productService.updateProduct(product);
			Link selfLink = linkTo(methodOn(ProductController.class).getProduct(product.getProductID())).withSelfRel();
			selfLink = selfLink.withHref(selfLink.getHref().replace("${url.product}", urlProduct));
			Link brandLink = linkTo(methodOn(BrandController.class).getBrand(product.getBrand().getBrandID())).withRel("brand");
			brandLink = brandLink.withHref(brandLink.getHref().replace("${url.brand}", urlBrand));
			HttpHeaders httpHeader = new HttpHeaders();
			HttpStatus status = HttpStatus.OK;
			httpHeader.setLocation(URI.create(selfLink.getHref()));
			product.add(selfLink);
			product.add(brandLink);
			logger.info(String.format("The product %s was updated",product));
			ApiMessage<Product> apiMessage = new ApiMessage<Product>(LocalDateTime.now(),
					status,"Product was updated.",product,selfLink.getHref());
			return new ResponseEntity<ApiMessage<Product>>(apiMessage,httpHeader,status);
		}
	}
	
	@RequestMapping(value="/{productID}",method=RequestMethod.DELETE)
	public ResponseEntity<?> deleteProduct(@PathVariable("productID") int productID) throws Throwable {
		if(productID<=0) {
			throw new IllegalArgumentException("The productID is invalid.");
		}else {
			//Check if there are orders items are added, then deactivate the product once there is and delete if there are none
			String message;
			if(orderItemService.countOrderItemsByProductID(productID)>0) {
				this.productService.deactivateProduct(productID);
				message = String.format("The productID %d was deactivated.", productID);
			}else {
				this.productService.deleteProduct(productID);
				message = String.format("The productID %d was deleted.", productID);
			}
			Link instanceLink = linkTo(methodOn(ProductController.class).getProduct(productID)).withRel("Instance");
			instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.product}", urlProduct));
			HttpHeaders httpHeader = new HttpHeaders();
			HttpStatus status = HttpStatus.OK;
			httpHeader.setLocation(URI.create(instanceLink.getHref()));
			logger.info(String.format(message, productID));
			ApiMessage<String> apiMessage = new ApiMessage<String>(LocalDateTime.now(),
					status,message, String.format("productID : %d", productID),instanceLink.getHref());
			return new ResponseEntity<ApiMessage<String>>(apiMessage,httpHeader,status);
		}
	}
}
