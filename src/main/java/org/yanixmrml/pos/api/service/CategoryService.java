package org.yanixmrml.pos.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yanixmrml.pos.api.model.Category;
import org.yanixmrml.pos.api.repository.CategoryRepository;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	public CategoryService() {
		super();
	}
	
	public List<Category> getCategories(){
		List<Category> categoryList = new ArrayList<Category>();
		this.categoryRepository.findAll().forEach(category->categoryList.add(category));
		return categoryList;
	}
	
	public Category getCategory(int categoryID) {
		return this.categoryRepository.findById(categoryID).get();
	}
	
	public void addCategory(Category category) {
		this.categoryRepository.save(category);
	}
	
	public void updateCategory(Category category) {
		this.categoryRepository.save(category);
	}
	
	public void deleteCategory(int categoryID) {
		this.categoryRepository.deleteById(categoryID);
	}
	
	public void deactivateCategory(int categoryID) {
		this.categoryRepository.deactivateCategory(categoryID);
	}
	
}
