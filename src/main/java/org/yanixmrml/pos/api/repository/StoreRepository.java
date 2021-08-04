package org.yanixmrml.pos.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import org.yanixmrml.pos.api.model.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer>{
	@Procedure(value="count_store_entities")
	public int countStoreAssociatedEntities(int storeID);
}
