package org.yanixmrml.pos.api.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
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
@Entity(name="orderItem")
@Table(name="order_items")
public class OrderItem extends RepresentationModel<OrderItem>{
	
	@EmbeddedId
	@Getter
	@Setter
	@AttributeOverrides({
		@AttributeOverride( name = "orderID", column = @Column(name = "order_id")),
		@AttributeOverride( name = "productID", column = @Column(name = "product_id"))  
	})
	private OrderItemID orderItemID;
	
	@Column(name="quantity")
	@Getter
	@Setter
	private double quantity;
	
	@Column(name="list_price")
	@Getter
	@Setter
	private double listPrice;
	
	@Column(name="discount")
	@Getter
	@Setter
	private double discount;
	
	@Column(name="last_updated")
	@Getter
	@Setter
	private Date lastUpdated;

	@ManyToOne
	@JoinColumn(name="product_id")
	@MapsId("productID")
	@Getter
	@Setter	
	private Product product;
}
