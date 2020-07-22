package com.company.shortenurl.shortenurl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
public class ShortenUrlApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ShortenUrlApplication.class, args);
    }

}
