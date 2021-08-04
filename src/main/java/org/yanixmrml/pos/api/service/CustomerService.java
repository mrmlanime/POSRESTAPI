package org.yanixmrml.pos.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yanixmrml.pos.api.model.Customer;
import org.yanixmrml.pos.api.repository.CustomerRepository;

@Service
public class CustomerService {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	public List<Customer> getCustomers(){
		List<Customer> customerList = new ArrayList<Customer>();
		this.customerRepository.findAll().forEach(customer -> customerList.add(customer));
		return customerList;
	}
	
	public Customer getCustomer(int customerID) {
		return this.customerRepository.findById(customerID).get();
	}
	
	public void addCustomer(Customer customer) {
		this.customerRepository.save(customer);
	}
	
	public void updateCustomer(Customer customer) {
		this.customerRepository.save(customer);
	}
	
	public void deleteCustomer(int customerID) {
		this.customerRepository.deleteById(customerID);
	}
	
	public void deactivateCustomer(int customerID) {
		this.customerRepository.deactivateCustomer(customerID);
	}
	
}
