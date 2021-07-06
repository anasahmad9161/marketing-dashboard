package io.gupshup.mdb.service.impl;

import io.gupshup.mdb.exceptions.InvalidRequestException;
import org.apache.logging.log4j.util.Strings;

import static io.gupshup.mdb.constants.ServiceConstants.EMPTY_FIELD;
import static java.util.Collections.singletonList;

public class ServiceCommonValidations {

	public static void validateField(String field, String fieldName){
		if (Strings.isBlank(field)) throw new InvalidRequestException(singletonList(EMPTY_FIELD + fieldName));
	}

	private ServiceCommonValidations(){
		throw new IllegalStateException("Static Class");
	}
}
