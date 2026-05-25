package com.example.usermanagement.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({CorsProperties.class, SecurityProperties.class})
public class AppConfig {
}
