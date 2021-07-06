package io.gupshup.mdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MarketingDashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarketingDashboardApplication.class, args);
	}

}
