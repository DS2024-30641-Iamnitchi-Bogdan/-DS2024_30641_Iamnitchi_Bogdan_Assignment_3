package org.biamn.ds2024.user_microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UserMicroserviceApplication {

	public static void main(String[] args) {
//		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
		SpringApplication.run(UserMicroserviceApplication.class, args);
	}

}
