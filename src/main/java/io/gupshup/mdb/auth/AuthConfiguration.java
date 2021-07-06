package io.gupshup.mdb.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static io.gupshup.mdb.constants.APIConstants.API;
import static io.gupshup.mdb.constants.APIConstants.USER;
import static io.gupshup.mdb.constants.APIConstants.VERSION;

@Configuration
public class AuthConfiguration implements WebMvcConfigurer {

	@Autowired
	AuthInterceptor authInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor).addPathPatterns(API + VERSION + USER + "/**");
	}
}
