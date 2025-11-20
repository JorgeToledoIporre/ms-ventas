package com.tienda.carrito.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI carritoAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Carrito Microservice API")
                .description("API REST para gestión de carrito de compras")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Tu Nombre")
                    .email("tu@email.com"))
                .license(new License()
                    .name("MIT")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8082")
                    .description("Servidor de desarrollo"),
                new Server()
                    .url("http://tu-ip-ec2:8082")
                    .description("Servidor de producción")
            ));
    }
}