package org.tpc.form_builder.service.dto;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.tpc.form_builder.models.User;
import org.tpc.form_builder.models.repository.UserRepository;

@Component
@Log4j2
public class AdminUserInitializer {

    @Bean
    public CommandLineRunner createAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsernameAndIsActiveTrue("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin1234"))
                        .roleId("ROLE_ADMIN")
                        .email("admin@tpc.com")
                        .build();
                userRepository.save(admin);
                log.info("Admin user created");
            }
        };
    }

}
