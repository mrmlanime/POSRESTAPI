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
@Entity(name="category")
@Table(name="categories")
public class Category extends RepresentationModel<Category> {
	@Id
	@Column(name="category_id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Getter
	@Setter
	private int categoryID;
	@Column(name="category_name")
	@Getter
	@Setter
	private String categoryName;
	@Column(name="status")
	@Getter
	@Setter
	private int status;
}
