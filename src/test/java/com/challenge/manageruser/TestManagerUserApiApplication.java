package com.challenge.manageruser;

import org.springframework.boot.SpringApplication;

public class TestManagerUserApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(ManagerUserApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
