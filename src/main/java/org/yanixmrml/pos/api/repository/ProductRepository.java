package org.yanixmrml.pos.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yanixmrml.pos.api.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>{
	
	@Query("SELECT p FROM product p WHERE p.productName LIKE %:productName%")
	public List<Product> getProductsByProductName(@Param("productName") String productName);
	
	
	@Query("SELECT p FROM product p WHERE p.category.categoryID = :categoryID")
	public List<Product> getProductsByCategory(@Param("categoryID") int categoryID);
	
	
	@Query("SELECT p FROM product p WHERE p.category.categoryID = :categoryID AND p.productName LIKE %:productName%")
	public List<Product> getProductsByCategoryAndProductName(@Param("categoryID") int categoryID,
				@Param("productName") String productName);
	
	@Query("SELECT count(p) FROM product p WHERE p.brand.brandID = :brandID")
	public int countProductsByBrandID(@Param("brandID") int brandID);
	
	@Query("SELECT count(o) FROM product p WHERE p.category.categoryID = :categoryID")
	public int countProductsByCategoryID(@Param("categoryID") int categoryID);
	
	@Modifying
	@Query("UPDATE product p SET p.status = 0 WHERE p.productID = :productID")
	public void deactivateProduct(@Param("productID") int productID);
}
