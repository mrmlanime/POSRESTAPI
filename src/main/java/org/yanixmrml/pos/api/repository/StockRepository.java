package org.yanixmrml.pos.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yanixmrml.pos.api.model.Stock;
import org.yanixmrml.pos.api.model.StockID;

@Repository
public interface StockRepository extends JpaRepository<Stock, StockID>{

}
