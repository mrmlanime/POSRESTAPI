package org.yanixmrml.pos.api.model;

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
public class MenuItem {
	@Getter
	@Setter
	private String menuName;
	@Getter
	@Setter
	private String menuLink;
}
