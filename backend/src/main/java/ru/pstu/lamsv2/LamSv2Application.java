package ru.pstu.lamsv2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.pstu.lamsv2.security.JwtAuthenticationFilter;
import ru.pstu.lamsv2.security.JwtProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(JwtProperties.class)
public class LamSv2Application {

    public static void main(String[] args) {
        SpringApplication.run(LamSv2Application.class, args);
    }

}
