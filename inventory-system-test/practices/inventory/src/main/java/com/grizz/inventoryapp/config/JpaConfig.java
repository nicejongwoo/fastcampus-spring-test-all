package com.grizz.inventoryapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.ZonedDateTime;
import java.util.Optional;

@EnableJpaAuditing(dateTimeProviderRef = "auditDateTimeProvider")
@Configuration
public class JpaConfig {

    @Bean
    public DateTimeProvider auditDateTimeProvider() {
        return () -> Optional.of(ZonedDateTime.now());
    }

}
