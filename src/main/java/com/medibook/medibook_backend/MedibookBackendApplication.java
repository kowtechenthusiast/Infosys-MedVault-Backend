package com.medibook.medibook_backend;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MedibookBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(MedibookBackendApplication.class, args);
	}
}

