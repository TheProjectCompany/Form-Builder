package org.bnbdevelopers.form_builder.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.util.Optional;

@Configuration
@EnableMongoAuditing
public class MongoConfig {
    // Optional: Provide AuditorAware for createdBy / modifiedBy
//    @Bean
//    public AuditorAware<String> auditorProvider() {
//        return () -> Optional.of("system-user"); // or pull from security context
//    }x`
}