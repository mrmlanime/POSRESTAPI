package org.yanixmrml.pos.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

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
@EqualsAndHashCode(callSuper=false)
@ToString
@Entity(name="customer")
@Table(name="customers")
public class Customer extends RepresentationModel<Customer>{
	@Id
	@Column(name="customer_id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Getter
	@Setter
	private int customerID;
	
	@Column(name="first_name")
	@Getter
	@Setter
	@NotNull
	private String firstName;
	
	@Column(name="last_name")
	@Getter
	@Setter
	@NotNull
	private String lastName;
	
	@Column(name="contact_number")
	@Getter
	@Setter
	private String contactNumber;
	
	@Column(name="email")
	@Getter
	@Setter
	@Email
	private String email;
	
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

	@Column(name="status")
	@Getter
	@Setter
	private int status;
}
