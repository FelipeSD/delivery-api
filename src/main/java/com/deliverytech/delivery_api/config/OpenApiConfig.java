package com.deliverytech.delivery_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Delivery Tech API")
            .version("1.0.0")
            .description(
                "API REST para sistema de delivery de comida com gerenciamento de restaurantes, produtos e pedidos")
            .contact(new Contact()
                .name("Delivery Tech Team")
                .email("contato@deliverytech.com")
                .url("https://deliverytech.com"))
            .license(new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .components(new Components().addSecuritySchemes("bearerAuth",
            new SecurityScheme()
                .name("bearerAuth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")))
        .servers(List.of(
            new Server()
                .url("http://localhost:8080")
                .description("Servidor de Desenvolvimento"),
            new Server()
                .url("https://api.deliverytech.com")
                .description("Servidor de Produção")))
        .tags(List.of(
            new Tag().name("Restaurantes").description("Operações relacionadas a restaurantes"),
            new Tag().name("Produtos").description("Operações relacionadas a produtos"),
            new Tag().name("Pedidos").description("Operações relacionadas a pedidos"),
            new Tag().name("Clientes").description("Operações relacionadas a clientes")));
  }
}