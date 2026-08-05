package com.altencir.tenantbilling;

import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TenantBillingApplication {
    public static void main(String[] args) { SpringApplication.run(TenantBillingApplication.class, args); }

    @Bean Clock clock() { return Clock.systemUTC(); }
}
