package org.tpc.form_builder.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info= @Info(
                title = "TPC Form Builder Service Documentation",
                version = "v1",
                description = "This is documentation of Form Builder Service APIs"
        ),
        security = @SecurityRequirement(
                name = "auth"
        ),
        servers = {@Server(url = "/form-builder/", description = "Default Server URL")}
)
@SecurityScheme(
        name = "auth",
        in = SecuritySchemeIn.HEADER,
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@Configuration
public class SwaggerConfig {
}
