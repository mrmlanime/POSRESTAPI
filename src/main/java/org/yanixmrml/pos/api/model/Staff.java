package org.yanixmrml.pos.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Entity(name="staff")
@Table(name="staffs")
public class Staff extends RepresentationModel<Staff>{
	@Id
	@Column(name="staff_id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Getter
	@Setter
	private int staffID;
	
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
	
	@Column(name="email")
	@Getter
	@Setter
	@Email
	private String email;
	
	@ManyToOne(targetEntity=Store.class)
	@JoinColumn(name="store_id")
	@Getter
	@Setter
	private Store store;
	
	//For Useraccount
	@Column(name="username")
	@Getter
	@Setter
	@NotNull
	private String username;
	
	@JsonIgnore
	@Column(name="password")
	@Getter
	@Setter
	private String password;
	
	@Column(name="status")
	@Getter
	@Setter
	private int status;
}
