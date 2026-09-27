package com.fixly.fixlybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(
        exclude = UserDetailsServiceAutoConfiguration.class
)
public class FixlyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FixlyBackendApplication.class, args);
    }

}
