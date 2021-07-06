package io.gupshup.mdb.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;

@Component
public class RequestLogInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(RequestLogInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
	                         Object handler) {
		long startTime = Instant.now().toEpochMilli();
		logger.info("METHOD={} :: URL={} :: Start Time={}", request.getMethod(), request.getRequestURL(), startTime);
		request.setAttribute("startTime", startTime);
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
	                            Exception ex) {
		long startTime = (Long) request.getAttribute("startTime");
		logger.info("METHOD={} :: URL={} :: Time Taken={} ms :: Response Status={}", request.getMethod(),
		            request.getRequestURL(), (Instant.now().toEpochMilli() - startTime), response.getStatus());
	}
}
