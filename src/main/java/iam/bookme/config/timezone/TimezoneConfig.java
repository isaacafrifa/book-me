package iam.bookme.config.timezone;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.TimeZone;

@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TimezoneConfig {

    /**
     * Initializes application-wide timezone and locale settings.
     * This runs at application startup to ensure consistent date/time handling
     * and formatting throughout the application lifecycle.
     * - UTC timezone ensures consistent timestamp storage and processing
     * - UK locale provides standardized date and number formatting
     */
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Locale.setDefault(Locale.UK);

        log.info("Application configured with timezone: {} and locale: {}",
                TimeZone.getDefault().getID(),
                Locale.getDefault());
    }
}
