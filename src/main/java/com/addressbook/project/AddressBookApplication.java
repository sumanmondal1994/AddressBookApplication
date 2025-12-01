package com.addressbook.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
	    info = @Info(
	        title = "Address Book Application",
	        description = "REST API for managing address books and contacts",
	        version = "1.0.0",
	        contact = @Contact(
	            name = "Suman Mondal",
	            email = "mondal.suman0504@gmail.com"
	        )
	    )
	)
public class AddressBookApplication {

	public static void main(String[] args) {
		SpringApplication.run(AddressBookApplication.class, args);
	}

}
