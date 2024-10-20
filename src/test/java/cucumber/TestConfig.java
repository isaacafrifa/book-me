package cucumber;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Test configuration class that sets up the Spring context for testing.
 * Provides a RestTemplate bean and scans for components in the "cucumber.context" package.
 */
@Configuration
@ComponentScan(basePackages = "cucumber.context")
public class TestConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
