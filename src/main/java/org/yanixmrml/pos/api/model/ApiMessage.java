package org.yanixmrml.pos.api.model;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ApiMessage<T>{
	@JsonFormat(shape=JsonFormat.Shape.STRING,pattern="dd-MM-yyyy hh:mm:ss")
	@Getter
	@Setter
	private LocalDateTime timestamp;
	@Getter
	@Setter
	private HttpStatus status;
	
	@Getter
	@Setter
	private String title;
	
	@Getter
	@Setter
	private T detail;
	
	@Getter
	@Setter
	private String instance;
}
