package org.yanixmrml.pos.api.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.NameNotFoundException;

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
import org.yanixmrml.pos.api.model.Brand;
import org.yanixmrml.pos.api.service.BrandService;
import org.yanixmrml.pos.api.service.ProductService;
@RestController("BrandController${vs}")
@RequestMapping("${url.brand}")
public class BrandController {
	
	@Autowired
	private BrandService brandService;
	@Autowired
	private ProductService productService;
	@Value("${url.brand}")
	private String urlBrand;
	private Logger logger = LoggerFactory.getLogger(BrandController.class);
	
	@GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> getBrands() throws Throwable {
		//Implement a pagination here
		List<Brand> brandList = brandService.getBrands();
		Link selfLink = linkTo(methodOn(BrandController.class).getBrands()).withSelfRel();
		selfLink = selfLink.withHref(selfLink.getHref().replace("${url.brand}", urlBrand));
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(URI.create(selfLink.getHref()));
		brandList.stream().map(b->{ 
			try {
				Link link = linkTo(methodOn(BrandController.class).getBrand(b.getBrandID())).withSelfRel();
				b.add(link.withHref(link.getHref().replace("${url.brand}", urlBrand)));
			} catch (Throwable e) {
				throw new RuntimeException("Problem with adding with the link. " + e.getLocalizedMessage());
			}
			return b;
		}).collect(Collectors.toList());
		logger.info("Brand list is fetched.");
		return new ResponseEntity<List<Brand>>(brandList,httpHeaders,HttpStatus.OK);	
	}
	
	@GetMapping(value="/{brandID}")
	public ResponseEntity<?> getBrand(@PathVariable("brandID") int brandID) throws Throwable{
		if(brandID<=0) {
			throw new NameNotFoundException(String.format("The brandID: %d not found.",brandID));
		}else {
			Brand brand = this.brandService.getBrand(brandID); 
			Link link = linkTo(methodOn(BrandController.class).getBrand(brandID)).withSelfRel();
			link = link.withHref(link.getHref().replace("${url.brand}", urlBrand));
			//System.out.print("Template - " + link.getTemplate().toString());
			HttpHeaders httpHeader = new HttpHeaders();
			httpHeader.setLocation(URI.create(link.getHref()));
			brand.add(link);
			logger.info(String.format("Brand %s is Found.", brand));
			return new ResponseEntity<Brand>(brand,httpHeader,HttpStatus.OK);
		}
	}
	
	@PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE},
			consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> addBrand(@RequestBody @Validated Brand brand) throws Throwable{
		if(brand==null) {
			throw new IllegalArgumentException(String.format("Brand %s value is not valid.", brand));
		}else {
			this.brandService.addBrand(brand);
			HttpStatus status = HttpStatus.CREATED;
			Link link = linkTo(methodOn(BrandController.class).getBrand(brand.getBrandID())).withSelfRel();
			link = link.withHref(link.getHref().replace("${url.brand}", urlBrand));
			HttpHeaders httpHeader = new HttpHeaders();
			String message = String.format("Brand %s was added.",brand);
			httpHeader.setLocation(URI.create(link.getHref()));
			brand.add(link);
			logger.info(message);
			ApiMessage<String> apiMessage = new ApiMessage<String>(LocalDateTime.now(),
					status,"Brand Created",message,link.getHref());
			return new ResponseEntity<ApiMessage<String>>(apiMessage,httpHeader,status);
		}
	}
	
	@PutMapping(value="/{brandID}",produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE},
			consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> updateBrand(@PathVariable("brandID") int brandID, 
			@RequestBody @Validated Brand brand) throws Throwable {
		if(brand==null || brandID<=0) {
			throw new IllegalArgumentException(String.format("Brand %s or brandID value is not valid.", brand));
		}else {
			brand.setBrandID(brandID);
			this.brandService.updateBrand(brand);
			HttpStatus status = HttpStatus.OK;
			Link link = linkTo(methodOn(BrandController.class).getBrand(brandID)).withSelfRel();
			link = link.withHref(link.getHref().replace("${url.brand}", urlBrand));
			HttpHeaders httpHeader = new HttpHeaders();
			httpHeader.setLocation(URI.create(link.getHref()));
			brand.add(link);
			logger.info(String.format("Brand %s was updated.", brand));
			ApiMessage<Brand> apiMessage = new ApiMessage<Brand>(LocalDateTime.now(),
					status,"Brand was updated.",brand,link.getHref());
			return new ResponseEntity<ApiMessage<Brand>>(apiMessage,httpHeader,status);
		}
	}
	
	//Avoid using link.toURI - There's an issue with their mapping implementation -> map former key is still used
	@DeleteMapping(value="/{brandID}",produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE},
			consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> deleteBrand(@PathVariable("brandID") int brandID) {
		if(brandID<=0) {
			throw new IllegalArgumentException("The brandID is invalid");
		}else {
			//check if the brand has been used if not then deleted it if there is then deactivate it		
			Link selfLink = linkTo(methodOn(BrandController.class).deleteBrand(brandID)).withSelfRel();
			selfLink = selfLink.withHref(selfLink.getHref().replace("${url.brand}", urlBrand));
			HttpStatus status = HttpStatus.OK;
			HttpHeaders httpHeader = new HttpHeaders();
			String message;
			if(this.productService.countProductsByBrandID(brandID)>0) {
				this.brandService.deactivateBrand(brandID); //set to status 0
				message = "Brand was deactivated. Brand has products associated with it.";
			}else {
				this.brandService.deleteBrand(brandID);	
				message = "Brand was deleted.";
			}
			httpHeader.setLocation(URI.create(selfLink.getHref()));
			ApiMessage<String> apiMessage = new ApiMessage<String>(LocalDateTime.now(),
					status,message,String.format("brandID: %d",brandID),selfLink.getHref());
			return new ResponseEntity<ApiMessage<String>>(apiMessage,httpHeader,status);	
		}
	}
	
}
