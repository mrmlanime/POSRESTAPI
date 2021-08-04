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
import org.yanixmrml.pos.api.model.Stock;
import org.yanixmrml.pos.api.model.StockID;
import org.yanixmrml.pos.api.service.StockService;

@RestController("StockController${vs}")
@RequestMapping("${url.stock}")
public class StockController {

	@Autowired
	private StockService stockService;
	@Value("${url.stock}")
	private String urlStock;
	@Value("${url.store}")
	private String urlStore;
	@Value("${url.product}")
	private String urlProduct;
	private Logger logger = LoggerFactory.getLogger(StockController.class);
	
	@GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> getStocks() throws Throwable{
		//Pagination and Filtering here
		List<Stock> stockList = this.stockService.getStocks();
		Link instanceLink = linkTo(methodOn(StockController.class).getStocks()).withRel("instance");
		instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.stock}", urlStock));
		HttpHeaders httpHeader = new HttpHeaders();
		httpHeader.setLocation(URI.create(instanceLink.getHref()));
		stockList.stream().map(s->{
			try {
				Link selfLink = linkTo(methodOn(StockController.class).getStock(s.getStockID().getStoreID(),
						s.getStockID().getProductID())).withSelfRel();
				selfLink = selfLink.withHref(selfLink.getHref().replace("${url.stock}", urlStock));
				Link storeLink = linkTo(methodOn(StoreController.class).getStore(s.getStockID().getStoreID())).withRel("store");
				storeLink = storeLink.withHref(storeLink.getHref().replace("${url.store}", urlStore));
				Link productLink = linkTo(methodOn(ProductController.class).getProduct(s.getStockID().getProductID())).withRel("product");
				productLink = productLink.withHref(productLink.getHref().replace("${url.product}", urlProduct));
				s.add(selfLink);
				s.add(storeLink);
				s.add(productLink);
			}catch(Throwable e) {
				throw new RuntimeException(e.getLocalizedMessage());
			}
			return s;
		}).collect(Collectors.toList());
		logger.info("The stock list was fetched.");
		return new ResponseEntity<List<Stock>>(stockList,httpHeader,HttpStatus.OK);
	}
	
	@GetMapping(value="/{storeID}/{productID}",produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> getStock(@PathVariable("storeID") int storeID,
			@PathVariable("productID") int productID) throws Throwable {
		if(storeID <= 0 || productID <= 0) {
			throw new IllegalArgumentException("The storeID or productID is not valid");
		}else {
			Stock stock = this.stockService.getStock(new StockID(storeID,productID));
			if(stock==null) {
				throw new NullPointerException("The stock is not found");
			}else {
				Link selfLink = linkTo(methodOn(StockController.class).getStock(storeID, productID)).withSelfRel();
				selfLink = selfLink.withHref(selfLink.getHref().replace("${url.stock}", urlStock));
				Link storeLink = linkTo(methodOn(StoreController.class).getStore(stock.getStockID().getStoreID())).withRel("store");
				storeLink = storeLink.withHref(storeLink.getHref().replace("${url.store}", urlStore));
				Link productLink = linkTo(methodOn(ProductController.class).getProduct(stock.getStockID().getProductID())).withRel("product");
				productLink = productLink.withHref(productLink.getHref().replace("${url.product}", urlProduct));
				HttpHeaders httpHeader = new HttpHeaders();
				httpHeader.setLocation(URI.create(selfLink.getHref()));
				stock.add(selfLink);
				stock.add(storeLink);
				stock.add(productLink);
				logger.info(String.format("The stock %s was found",stock));
				return new ResponseEntity<Stock>(stock,httpHeader,HttpStatus.OK);
			}
		}
	}
	
	@PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> addStock(@RequestBody @Validated Stock stock) throws Throwable {
		if(stock==null){
			throw new IllegalArgumentException("The stock submitted is invalid.");
		}else {
			this.stockService.addStock(stock);
			Link instanceLink = linkTo(methodOn(StockController.class).addStock(stock)).withRel("instance");
			Link selfLink = linkTo(methodOn(StockController.class).getStock(stock.getStockID().getStoreID(), stock.getStockID().getProductID())).withSelfRel();
			selfLink = selfLink.withHref(selfLink.getHref().replace("${url.stock}", urlStock));
			Link storeLink = linkTo(methodOn(StoreController.class).getStore(stock.getStockID().getStoreID())).withRel("store");
			storeLink = storeLink.withHref(storeLink.getHref().replace("${url.store}", urlStore));
			Link productLink = linkTo(methodOn(ProductController.class).getProduct(stock.getStockID().getProductID())).withRel("product");
			productLink = productLink.withHref(productLink.getHref().replace("${url.product}", urlProduct));
			HttpHeaders httpHeader = new HttpHeaders();
			HttpStatus status = HttpStatus.CREATED;
			httpHeader.setLocation(URI.create(instanceLink.getHref()));
			String message = "The stock was added.";
			stock.add(selfLink);
			stock.add(storeLink);
			stock.add(productLink);
			logger.info(message);
			ApiMessage<Stock> apiMessage = new ApiMessage<Stock>(LocalDateTime.now(),
					status,message,stock,instanceLink.getHref());
			return new ResponseEntity<ApiMessage<Stock>>(apiMessage,httpHeader,status);
		}
	}
	
	@PutMapping(value="/{storeID}/{productID}",produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> updateStock(@PathVariable("storeID") int storeID,@PathVariable("productID") int productID,
			@RequestBody @Validated Stock stock) throws Throwable {
		if(storeID<=0||productID<=0) {
			throw new IllegalArgumentException("The storeID or productID is not valid");
		}else {
			stock.setStockID(new StockID(storeID,productID));
			this.stockService.updateStock(stock);
			Link instanceLink = linkTo(methodOn(StockController.class).updateStock(storeID,productID,stock)).withRel("instance");
			instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.stock}", urlStock));
			Link selfLink = linkTo(methodOn(StockController.class).getStock(stock.getStockID().getStoreID(), stock.getStockID().getProductID())).withSelfRel();
			selfLink = selfLink.withHref(selfLink.getHref().replace("${url.stock}", urlStock));
			Link storeLink = linkTo(methodOn(StoreController.class).getStore(stock.getStockID().getStoreID())).withRel("store");
			storeLink = storeLink.withHref(storeLink.getHref().replace("${url.store}", urlStore));
			Link productLink = linkTo(methodOn(ProductController.class).getProduct(stock.getStockID().getProductID())).withRel("product");
			productLink = productLink.withHref(productLink.getHref().replace("${url.product}", urlProduct));
			HttpHeaders httpHeader = new HttpHeaders();
			HttpStatus status = HttpStatus.OK;
			httpHeader.setLocation(URI.create(instanceLink.getHref()));
			String message = String.format("The stock %s was updated", stock);
			stock.add(selfLink);
			stock.add(storeLink);
			stock.add(productLink);
			logger.info(message);
			ApiMessage<String> apiMessage = new ApiMessage<String>(LocalDateTime.now(),
					status,"Stock Update",message,instanceLink.getHref());
			return new ResponseEntity<ApiMessage<String>>(apiMessage,httpHeader,status);
		}
	}
	
	@DeleteMapping(value="/{storeID}/{productID}",produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> deleteStock(@PathVariable("storeID") int storeID, 
			@PathVariable("productID") int productID) throws Throwable {
		if(storeID<=0||productID<=0){
			throw new IllegalArgumentException("The storeID or productID is not valid");
		}else {
			this.stockService.deleteStock(new StockID(storeID,productID));
			Link instanceLink = linkTo(methodOn(StockController.class).deleteStock(storeID,productID)).withRel("instance");
			instanceLink = instanceLink.withHref(instanceLink.getHref().replace("${url.stock}", urlStock));
			HttpHeaders httpHeader = new HttpHeaders();
			HttpStatus status = HttpStatus.OK;
			httpHeader.setLocation(URI.create(instanceLink.getHref()));
			String message = String.format("The stock with storeID: %d, productID: %d was deleted", storeID, productID);
			logger.info(message);
			ApiMessage<String> apiMessage = new ApiMessage<String>(LocalDateTime.now(),
					status,"Stock Delete",message,instanceLink.getHref());
			return new ResponseEntity<ApiMessage<String>>(apiMessage,httpHeader,status);
		}
	}
	
}
