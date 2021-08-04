package org.yanixmrml.pos.api.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yanixmrml.pos.api.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

	@Query("SELECT count(o) FROM order o WHERE o.customer.customerID = :customerID")
	public int countOrdersByCustomerID(@Param("customerID") int customerID);

	@Query("SELECT count(o) FROM order o WHERE o.staff.staffID = :staffID")
	public int countOrdersByStaffID(@Param("staffID") int staffID);
	
	@Modifying
	@Query("UPDATE FROM order o SET o.orderStatus = :orderStatus WHERE o.orderID = :orderID")
	public void updateOrderStatus(@Param("orderStatus") int orderStatus, @Param("orderID") int orderID);
	
}
