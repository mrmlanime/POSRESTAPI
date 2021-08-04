package org.yanixmrml.pos.api.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
@Entity(name="stock")
@Table(name="stocks")
public class Stock extends RepresentationModel<Stock>{
	@EmbeddedId
	@Getter
	@Setter
	private StockID stockID;
	
	@Column(name="quantity")
	@Getter
	@Setter
	private double quantity;
}
