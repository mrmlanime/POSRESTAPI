package org.yanixmrml.pos.api.exception;

import java.net.URI;
import java.time.LocalDateTime;

import javax.naming.NameNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.UriComponentsBuilder;
import org.yanixmrml.pos.api.model.ApiMessage;

import javassist.NotFoundException;

@ControllerAdvice
public class PosApiExceptionHandler extends ResponseEntityExceptionHandler{

	private Logger logger = LoggerFactory.getLogger(PosApiExceptionHandler.class);
	
	@ExceptionHandler({Throwable.class})
	public ResponseEntity<?> handleThrowable(Throwable ex, WebRequest request){
		HttpHeaders httpHeader = new HttpHeaders();
		URI link = UriComponentsBuilder.fromPath(request.getContextPath()).build().toUri();
		httpHeader.setLocation(link);
		HttpStatus status = HttpStatus.BAD_REQUEST;
		if(ex instanceof NullPointerException) {
			status = HttpStatus.NOT_ACCEPTABLE;
		}else if(ex instanceof NameNotFoundException
				|| ex instanceof NotFoundException) {
			status = HttpStatus.NOT_FOUND;
		}else if(ex instanceof IllegalArgumentException) {
			status = HttpStatus.NOT_ACCEPTABLE;
		}else {
			status = HttpStatus.BAD_REQUEST;
		}
		logger.error(String.format("Error: %s", ex.getLocalizedMessage() + " - " + ex.getMessage()));
		ApiMessage<String> apiMessage = new ApiMessage<String>(LocalDateTime.now(),status,
				ex.getClass().toString(),ex.getMessage(),link.toString());
		return new ResponseEntity<ApiMessage<String>>(apiMessage,httpHeader,status);
	}	
	
}
