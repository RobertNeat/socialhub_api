package com.springboot.socialhub_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SocialhubApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(SocialhubApiApplication.class, args);
	}

}

/*
* Aby skorzystać z dokumentacji swagger z paczki OpenApi (OpenApi zawiera w sobie Swagger) należy:
* - w przeglądarce (GET):http://localhost:8080/swagger (domyślnie: http://localhost:8080/v3/api-docs)
* - w przeglądarce (HTML UI): http://localhost:8080/swagger.html (domyślnie: http://localhost:8080/swagger-ui/index.html)
* (^ domyślne trasy dokumentacji zmieniliśmy za pomocą application.properties)
* */


/*
* Adnotacje swagger ( a w naszym przypadku springdoc):
* -jako referencja  --- https://docs.swagger.io/swagger-core/v1.5.0/apidocs/
* -adnotacje ktore mozna używać --- https://springdoc.org/#migrating-from-springfox
* */