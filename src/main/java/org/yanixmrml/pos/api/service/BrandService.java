package org.yanixmrml.pos.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yanixmrml.pos.api.model.Brand;
import org.yanixmrml.pos.api.repository.BrandRepository;

@Service
public class BrandService {
	
	@Autowired
	private BrandRepository brandRepository;
	
	public BrandService() {
		super();
	}
	
	public List<Brand> getBrands(){
		List<Brand> brandList = new ArrayList<Brand>();
		this.brandRepository.findAll().forEach(brand -> brandList.add(brand));
		return brandList;
	}
	
	public Brand getBrand(int brandID) {
		return this.brandRepository.findById(brandID).get();
	}
	
	public void addBrand(Brand brand) {
		this.brandRepository.save(brand);
	}
	
	public void updateBrand(Brand brand) {
		this.brandRepository.save(brand);
	}
	
	public void deleteBrand(int brandID) {
		this.brandRepository.deleteById(brandID);
	}
	
	public void deactivateBrand(int brandID) {
		this.brandRepository.deactivateBrand(brandID);
	}
}
