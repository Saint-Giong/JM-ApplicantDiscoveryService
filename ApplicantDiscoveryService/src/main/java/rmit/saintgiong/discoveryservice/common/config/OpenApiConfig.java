package rmit.saintgiong.discoveryservice.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// OpenAPI/Swagger configuration for API documentation.
@Configuration
@OpenAPIDefinition(servers = {
        @Server(url = "/v1/discovery/", description = "To Gateway Endpoint"),
        @Server(url = "http://localhost:${server.port}", description = "Direct Discovery URL")
})
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Applicant Discovery Service API")
                        .description("API for managing Search Profiles - allows companies to define and save applicant search criteria")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Saint Giong Team")
                                .email("support@saintgiong.rmit.edu.vn"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWE")
                                .description("JWE Access Token for authenticated endpoints")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
