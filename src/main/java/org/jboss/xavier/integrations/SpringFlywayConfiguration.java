package org.jboss.xavier.integrations;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration
public class SpringFlywayConfiguration {
    /**
     * Override default flyway initializer to do nothing
     */
    @Bean
    FlywayMigrationInitializer flywayInitializer(Flyway flyway) {
        return new FlywayMigrationInitializer(flyway, (f) ->{} );
    }


    /**
     * Create a second flyway initializer to run after jpa has created the schema
     */
    @Bean
    FlywayMigrationInitializer delayedFlywayInitializer(Flyway flyway, EntityManagerFactory eManagerFactory) {
        return new FlywayMigrationInitializer(flyway, null);
    }

}
