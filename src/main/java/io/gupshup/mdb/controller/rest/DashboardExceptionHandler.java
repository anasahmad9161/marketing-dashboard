package io.gupshup.mdb.controller.rest;

import com.google.i18n.phonenumbers.NumberParseException;
import io.gupshup.mdb.errors.RestError;
import io.gupshup.mdb.exceptions.AuthenticationException;
import io.gupshup.mdb.exceptions.CustomRuntimeException;
import io.gupshup.mdb.exceptions.FileReadException;
import io.gupshup.mdb.exceptions.InvalidRequestException;
import io.gupshup.mdb.exceptions.ResourceAlreadyExistsException;
import io.gupshup.mdb.exceptions.ResourceNotFountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class DashboardExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(DashboardExceptionHandler.class);

	@ExceptionHandler({FileReadException.class, InvalidRequestException.class, ResourceNotFountException.class,
			NumberParseException.class, ResourceAlreadyExistsException.class, RuntimeException.class,
			CustomRuntimeException.class, AuthenticationException.class, MethodArgumentNotValidException.class})
	public final ResponseEntity<RestError> handleException(Exception ex) {
		logger.error("Exception Caught : " + ex.getClass() + " with message : " + ex.getMessage());
		if (ex instanceof FileReadException) {
			return new ResponseEntity<>(new RestError("InvalidFile", ex.getMessage()), BAD_REQUEST);
		} else if (ex instanceof InvalidRequestException) {
			return new ResponseEntity<>(new RestError("InvalidRequest", ex.getMessage()), BAD_REQUEST);
		} else if (ex instanceof ResourceNotFountException) {
			return new ResponseEntity<>(new RestError("NotFound", ex.getMessage()), NOT_FOUND);
		} else if (ex instanceof NumberParseException) {
			return new ResponseEntity<>(new RestError("InvalidRecord", ex.getMessage()), BAD_REQUEST);
		} else if (ex instanceof ResourceAlreadyExistsException) {
			return new ResponseEntity<>(new RestError("AlreadyExists", ex.getMessage()), BAD_REQUEST);
		} else if (ex instanceof CustomRuntimeException) {
			return new ResponseEntity<>(new RestError("UnknownException", ex.getMessage()), BAD_REQUEST);
		} else if (ex instanceof MethodArgumentNotValidException) {
			return new ResponseEntity<>(new RestError("InvalidRequest", getValidationMessage(
					(MethodArgumentNotValidException) ex)), BAD_REQUEST);
		} else if (ex instanceof AuthenticationException) {
			return new ResponseEntity<>(new RestError("FORBIDDEN", ex.getMessage()), FORBIDDEN);
		} else {
			return new ResponseEntity<>(new RestError("InternalError", ex.getMessage()), INTERNAL_SERVER_ERROR);
		}
	}

	private String getValidationMessage(MethodArgumentNotValidException ex){
		return ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(
				Collectors.joining(", "));
	}
}
