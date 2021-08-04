package org.yanixmrml.pos.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper=false)
@Entity(name="store")
@Table(name="stores")
public class Store extends RepresentationModel<Store>{
	@Id
	@Column(name="store_id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Getter
	@Setter
	private int storeID;
	
	@Column(name="store_name")
	@Getter
	@Setter
	private String storeName;
	
	@Column(name="contact_number")
	@Getter
	@Setter
	private String contactNumber;
	
	@Column(name="street_village")
	@Getter
	@Setter
	private String streetVillage;
	
	@Column(name="city")
	@Getter
	@Setter
	private String city;
	
	@Column(name="zip_code")
	@Getter
	@Setter
	private String zipCode;
}
