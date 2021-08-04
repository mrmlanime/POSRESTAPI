package org.yanixmrml.pos.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@XmlRootElement
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
@Entity(name="brand")
@Table(name="brands")
public class Brand extends RepresentationModel<Brand>{
	@Id
	@Column(name="brand_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Getter 
	@Setter
	private int brandID;
	
	@Column(name="brand_name")
	@Getter
	@Setter
	private String brandName;
	
	@Column(name="status")
	@Getter
	@Setter
	private int status;
}
