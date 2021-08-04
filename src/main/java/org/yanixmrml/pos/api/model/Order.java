package org.yanixmrml.pos.api.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Entity(name="order")
@Table(name="orders")
public class Order extends RepresentationModel<Order>{
	@Id
	@Column(name="order_id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Getter
	@Setter
	private int orderID;
	
	@Column(name="order_status")
	@Getter
	@Setter
	private int orderStatus;
	
	@Column(name="transaction_date")
	@Getter
	@Setter
	private Date transactionDate;
	
	@Column(name="shipped_date")
	@Getter
	@Setter
	private Date shippedDate;
	
	@ManyToOne
	@JoinColumn(name="customer_id")
	@Getter
	@Setter
	private Customer customer;

	@ManyToOne
	@JoinColumn(name="store_id")
	@Getter
	@Setter
	private Store store;
	
	@ManyToOne
	@JoinColumn(name="staff_id")
	@Getter
	@Setter
	private Staff staff; //transaction in-charge
}
