package br.com.wastenio.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Industrial Production Optimizer API")
                        .description("API for managing raw materials, products and industrial production optimization.")
                        .version("v1")
                        .contact(new Contact()
                                .name("Wastenio Silva")
                                .email("wastenio.silva@gmail.com")));
    }

}
