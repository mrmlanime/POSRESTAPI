package org.yanixmrml.pos.api.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yanixmrml.pos.api.model.OrderItem;
import org.yanixmrml.pos.api.model.OrderItemID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemID>{

	@Query("SELECT o FROM orderItem o WHERE o.orderItemID.orderID = :orderID")
	public List<OrderItem> findByOrderItemIdOrderId(@Param("orderID") int orderID);
	
	@Query("SELECT count(o) FROM orderItem o WHERE o.orderItemID.productID = :productID")
	public int countOrderItemsByProductID(@Param("productID") int productID);
	
	@Modifying
	@Query("DELETE FROM orderItem o WHERE o.orderItemID.orderID = :orderID")
	public void deleteAllOrderItemsByOrderID(@Param("orderID") int orderID);
}
