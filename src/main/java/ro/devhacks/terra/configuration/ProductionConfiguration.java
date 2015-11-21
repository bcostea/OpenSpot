package ro.devhacks.terra.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("production")
public class ProductionConfiguration {
}
