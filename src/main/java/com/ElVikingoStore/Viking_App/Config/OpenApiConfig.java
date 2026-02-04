package com.ElVikingoStore.Viking_App.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
@Configuration
public class OpenApiConfig {

    @Value("${server.address}")
    private String serverAddress;

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        String serverUrl = String.format("http://%s:%s%s",
                serverAddress,
                serverPort,
                contextPath.equals("/") ? "" : contextPath);

        Server server = new Server()
                .url(serverUrl)
                .description("Server URL");

        // Configuración del esquema de seguridad
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Ingrese el token JWT con el prefijo Bearer: Bearer <token>");

        // Requerimiento de seguridad global
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearer-jwt");

        return new OpenAPI()
                .servers(Arrays.asList(server))
                .info(new Info()
                        .title("Viking-App ApiREST")
                        .description("Documentación de mi API Rest\n\n" +
                                "Para autenticarte:\n" +
                                "1. Usa el endpoint /auth/login para obtener el token\n" +
                                "2. Copia el token devuelto\n" +
                                "3. Click en el botón 'Authorize' (🔓) arriba\n" +
                                "4. Pega el token en el campo 'Value' (incluye 'Bearer ')")
                        .version("1.0")
                        .contact(new Contact()
                                .name("D3XTRO12")
                                .email("mirazopablo@gmail.com")))
                .addSecurityItem(securityRequirement)
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", securityScheme));
    }

    @PostConstruct
    public void printOpenApiUrls() {
        String baseUrl = String.format("http://%s:%s%s",
                serverAddress,
                serverPort,
                contextPath.equals("/") ? "" : contextPath);

        System.out.println("\n========= OpenAPI URLs =========");
        System.out.println("Swagger UI: " + baseUrl + "/swagger-ui.html");
        System.out.println("API Docs: " + baseUrl + "/v3/api-docs");
        System.out.println("================================\n");
    }
}