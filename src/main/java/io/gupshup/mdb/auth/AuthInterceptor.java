package io.gupshup.mdb.auth;

import io.gupshup.mdb.exceptions.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static io.gupshup.mdb.constants.APIConstants.TOKEN;
import static io.gupshup.mdb.constants.APIConstants.USERID;
import static org.springframework.web.servlet.HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;

@Component
public class AuthInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
	private static final String INVALID_TOKEN = "Token Expired or Invalid";

	@Autowired
	private AuthenticationService authenticationService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		logger.info("Authenticating Request :: Method=" + request.getMethod() + " :: URL=" + request.getRequestURL());
		Map<String, String> map = (Map<String, String>) request.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		if(map == null || !map.containsKey(USERID)){
			logger.info("Invalid Request URL=" + request.getRequestURL());
			throw new AuthenticationException("Invalid Request URL");
		}
		logger.info(map.toString());
		String phoneNumber = map.get(USERID);
		boolean authenticated = authenticationService.validateAuthToken(phoneNumber, request.getHeader(TOKEN));
		if (authenticated) {
			logger.info("Auth Successful for Method=" + request.getMethod() + " :: URL=" + request.getRequestURL());
		} else {
			logger.info("Auth Failed for Method=" + request.getMethod() + " :: URL=" + request.getRequestURL());
			throw new AuthenticationException(INVALID_TOKEN);
		}
		return true;
	}
}