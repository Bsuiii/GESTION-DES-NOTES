package com.example.school_managment_system;

import com.example.school_managment_system.services.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SchoolManagmentSystemApplication {

	@Autowired
	private static ModuleService moduleService;
	public static void main(String[] args) {

		SpringApplication.run(SchoolManagmentSystemApplication.class, args);

	}

}
