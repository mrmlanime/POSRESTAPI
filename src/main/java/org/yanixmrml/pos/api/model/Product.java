package org.yanixmrml.pos.api.model;

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
@Entity(name="product")
@Table(name="products")
public class Product extends RepresentationModel<Product>{
	@Id
	@Column(name="product_id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Getter
	@Setter
	private int productID;
	
	@Column(name="product_name")
	@Getter
	@Setter
	private String productName;
	
	@Column(name="model")
	@Getter
	@Setter
	private String model;
	
	@Column(name="price")
	@Getter
	@Setter
	private double price;
	
	@Column(name="status")
	@Getter
	@Setter
	private int status;
	
	@ManyToOne
	@JoinColumn(name="brand_id")
	@Getter
	@Setter
	private Brand brand;
	
	@ManyToOne
	@JoinColumn(name="category_id")
	@Getter
	@Setter
	private Category category;
}
