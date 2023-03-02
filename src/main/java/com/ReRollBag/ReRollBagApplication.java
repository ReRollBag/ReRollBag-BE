package com.ReRollBag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ReRollBagApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReRollBagApplication.class, args);
	}

}
