package org.yanixmrml.pos.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yanixmrml.pos.api.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer>{

	@Modifying
	@Query("UPDATE customer c SET c.status = 0 WHERE c.customerID = :customerID")
	public void deactivateCustomer(@Param("customerID") int customerID);
}
