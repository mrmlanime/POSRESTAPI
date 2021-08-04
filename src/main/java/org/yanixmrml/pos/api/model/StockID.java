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
public class StockID implements Serializable{

	private static final long serialVersionUID = -7061424736947612547L;

	@Column(name="store_id")
	@Getter
	@Setter
	private int storeID;
	
	@Column(name="product_id")
	@Getter
	@Setter
	private int productID;
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return Objects.hash(this.storeID,this.productID);
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj==null||obj.getClass()!=this.getClass()) {
			return false;
		}
		if(this==obj) {
			return true;
		}
		StockID stockID = (StockID)obj;
		return (stockID.storeID==this.storeID
				&& stockID.productID==this.productID);
	}
}
