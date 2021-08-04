package org.yanixmrml.pos.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yanixmrml.pos.api.model.Stock;
import org.yanixmrml.pos.api.model.StockID;
import org.yanixmrml.pos.api.repository.StockRepository;

@Service
public class StockService {

	@Autowired
	private StockRepository stockRepository;
	
	public StockService() {
		super();
	}
	
	public List<Stock> getStocks(){
		List<Stock> stockList = new ArrayList<Stock>();
		this.stockRepository.findAll().forEach(stock -> stockList.add(stock));
		return stockList;
	}

	public Stock getStock(StockID stockID) {
		return this.stockRepository.findById(stockID).get();
	}
	
	public void addStock(Stock stock) {
		this.stockRepository.save(stock);
	}
	
	public void updateStock(Stock stock) {
		this.stockRepository.save(stock);
	}
	
	public void deleteStock(StockID stockID) {
		this.stockRepository.deleteById(stockID);
	}
}
