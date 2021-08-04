package org.yanixmrml.pos.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yanixmrml.pos.api.model.Staff;
import org.yanixmrml.pos.api.repository.StaffRepository;

@Service
public class StaffService {//implements UserDetailsService{

	@Autowired
	private StaffRepository staffRepository;
	
	public StaffService() {
		super();
	}
	
	public List<Staff> getStaffs(){
		List<Staff> staffList = new ArrayList<Staff>();
		this.staffRepository.findAll().forEach(staff->staffList.add(staff));
		return staffList;
	}
	
	public Staff getStaff(int staffID) {
		return staffRepository.findById(staffID).get();
	}
	
	public void addStaff(Staff staff) {
		this.staffRepository.save(staff);
	}
	
	public void updateStaff(Staff staff) {
		this.staffRepository.save(staff);
	}
	
	public void deleteStaff(int staffID) {
		this.deleteStaff(staffID);
	}
	
	/*
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		//Call REpository getStaffByUsername
		Optional<Staff> staff = staffRepository.getStaffByUsername(username);
		//System.out.println("Staff " + staff);
		//staff.orElseThrow(()->throw new UserNotFoundException(""));
		staff.orElseThrow(()->new UsernameNotFoundException("Not Found: " + username));
		return staff.map(POSUserDetails::new).get();
		//Note usage of Class::new refers to default constructor of the class and call it, ex
		//Class::method
		
	}*/

}
