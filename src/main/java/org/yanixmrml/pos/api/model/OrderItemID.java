package org.yanixmrml.pos.api.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Embeddable
public class OrderItemID implements Serializable{
	
	private static final long serialVersionUID = 9203365395079506004L;
	@Column(name="order_id")
	@Getter
	@Setter
	private int orderID;
	
	@Column(name="product_id")
	@Getter
	@Setter
	private int productID;
		
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return Objects.hash(this.orderID,this.productID);
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj==null || obj.getClass()!=this.getClass()) {
			return false;
		}
		if(this==obj) {
			return true;
		}
		OrderItemID orderItemID = (OrderItemID) obj;
		return(this.orderID==orderItemID.orderID
				&& this.productID==orderItemID.productID);
	}
}
