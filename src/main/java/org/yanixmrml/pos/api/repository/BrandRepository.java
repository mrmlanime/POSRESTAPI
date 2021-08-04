package org.yanixmrml.pos.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yanixmrml.pos.api.model.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer>{

	@Modifying
	@Query("UPDATE brand b SET b.status = 0 WHERE b.brandID = :brandID")
	public void deactivateBrand(@Param("brandID") int brandID);

}
