package com.challenge.manageruser.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    private static final String URL = "http://localhost:8080";

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(URL);
        devServer.setDescription("Development environment");

        Contact contact = new Contact();
        contact.setEmail("danilosilvap@outlook.com");
        contact.setName("Danilo Silva");
        contact.setUrl("https://danilosilva.vercel.app/");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info().title("Manager User API").version("0.0.1-SNAPSHOT").contact(contact).description("This API exposes endpoints to manage users.").license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}