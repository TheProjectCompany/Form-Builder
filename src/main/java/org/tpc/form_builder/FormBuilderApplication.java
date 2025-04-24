package org.tpc.form_builder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class FormBuilderApplication {

    public static void main(String[] args) {
        SpringApplication.run(FormBuilderApplication.class, args);
    }

}
