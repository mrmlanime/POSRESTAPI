package org.yanixmrml.pos.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yanixmrml.pos.api.model.Store;
import org.yanixmrml.pos.api.repository.StoreRepository;

@Service
public class StoreService {

	@Autowired
	private StoreRepository storeRepository;
	
	public StoreService() {
		super();
	}
	
	public List<Store> getStores(){
		List<Store> storeList = new ArrayList<Store>();
		this.storeRepository.findAll().forEach(store -> storeList.add(store));
		return storeList;
	}
	
	public Store getStore(int storeID) {
		return this.storeRepository.findById(storeID).get();
	}
	
	public void addStore(Store store) {
		this.storeRepository.save(store);
	}
	
	public void updateStore(Store store) {
		this.storeRepository.save(store);
	}
	
	public void deleteStore(int storeID) {
		this.storeRepository.deleteById(storeID);
	}
	
	public boolean canBeDeleted(int storeID) {
		return this.storeRepository.countStoreAssociatedEntities(storeID)<=0;
	}
	
}
