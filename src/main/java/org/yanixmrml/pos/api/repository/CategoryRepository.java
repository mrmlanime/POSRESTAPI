package org.yanixmrml.pos.api.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yanixmrml.pos.api.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{
	@Modifying
	@Query("UPDATE category c SET c.status = 0 WHERE c.categoryID = :categoryID")
	public void deactivateCategory(@Param("categoryID") int categoryID);
}
