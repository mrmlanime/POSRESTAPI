package org.yanixmrml.pos.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yanixmrml.pos.api.model.Product;
import org.yanixmrml.pos.api.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	public ProductService() {
		super();
	}
	
	public List<Product> getProducts(){
		List<Product> productList = new ArrayList<Product>();
		this.productRepository.findAll().forEach(product -> productList.add(product));
		return productList;
	}
	
	public Product getProduct(int productID) {
		return this.productRepository.findById(productID).get();
	}
	
	public void addProduct(Product product) {
		this.productRepository.save(product);
	}
	
	public void updateProduct(Product product) {
		this.productRepository.save(product);
	}
	
	public void deleteProduct(int productID) {
		this.productRepository.deleteById(productID);
	}
	
	public List<Product> getProductsByName(String productName){
		return this.productRepository.getProductsByProductName(productName);
	}
	
	public List<Product> getProductsByCategory(int categoryID){
		return this.productRepository.getProductsByCategory(categoryID);
	}
	
	public List<Product> getProductsByCategoryAndName(int categoryID,String productName){
		return this.productRepository.getProductsByCategoryAndProductName(categoryID, productName);
	}
	
	public int countProductsByBrandID(int brandID) {
		return this.productRepository.countProductsByBrandID(brandID);
	}
	
	public int countProductsByCategoryID(int categoryID) {
		return this.productRepository.countProductsByCategoryID(categoryID);
	}
	
	public void deactivateProduct(int productID) {
		this.productRepository.deactivateProduct(productID);
	}
	
}
