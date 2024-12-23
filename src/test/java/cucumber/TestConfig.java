package cucumber;

import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
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

    /**
     * Provides a NoOpCacheManager bean for testing environment.
     * Using NoOpCacheManager in tests is preferred because:
     * 1. It ensures test isolation by preventing cached data from affecting other tests
     * 2. It verifies actual database operations instead of cached results
     * @return A NoOpCacheManager instance that bypasses caching
     */
    @Bean
    public CacheManager cacheManager() {
        return new NoOpCacheManager();
    }
}
