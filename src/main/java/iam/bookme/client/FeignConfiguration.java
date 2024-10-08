package iam.bookme.client;

import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import feign.micrometer.MicrometerObservationCapability;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfiguration {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    /*
     * This will log the basic information along with request and response headers.
     * The basic info entails request method and URL and the response status code and execution time.
     * @return Logger.Level.HEADERS
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.HEADERS;
    }

    /*
     * This will set the connection and read timeout for the feign client.
     * @return Request.Options
     */
    @Bean
    public Request.Options options() {
        return new Request.Options(3000, TimeUnit.MILLISECONDS, 3000, TimeUnit.MILLISECONDS, true);
    }

    /*
     * This will enable the observation capability for the feign client.
     * @return MicrometerObservationCapability
     */
    @Bean
    public MicrometerObservationCapability micrometerObservationCapability(ObservationRegistry registry) {
        return new MicrometerObservationCapability(registry);
    }

}
