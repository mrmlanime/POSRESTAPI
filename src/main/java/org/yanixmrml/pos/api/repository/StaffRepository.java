package org.yanixmrml.pos.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yanixmrml.pos.api.model.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer>{
	
	//Note entity model name here is case sensitive so please copy the case and name correctly on the annotation entity name registered
	@Query("SELECT s FROM staff s WHERE s.username = :username")
	public Optional<Staff> getStaffByUsername(@Param("username") String username);
}
