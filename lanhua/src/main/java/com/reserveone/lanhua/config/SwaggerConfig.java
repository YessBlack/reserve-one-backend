package com.reserveone.lanhua.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Reserve One API")
                        .version("1.0.0")
                        .description("API REST para la gestión de usuarios, reservas, membresías, horarios, pagos y suscripciones del sistema Reserve One.")
                        .contact(new Contact()
                                .name("Reserve One Team")
                                .email("soporte@reserveone.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor local")
                ));
    }
}
