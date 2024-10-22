package cucumber.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iam.bookme.repository.BookingRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;
import static org.springframework.util.CollectionUtils.toMultiValueMap;

@Slf4j
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class CucumberService {

    @Autowired
    private TestContext context;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.api.host}")
    protected String apiHost;
    @Value("${app.api.version}")
    protected String apiVersion;
    @LocalServerPort
    protected int port;


    public Map<String, Object> getBookingRequestDto(String fieldToBeRemoved) {
        Map<String, Object> createRequestDto = createInitialBookingRequestDto();

        if (!Strings.isNullOrEmpty(fieldToBeRemoved)) {
            removeFieldIfExists(createRequestDto, fieldToBeRemoved);
        } else {
            log.info("Not removing any field from BookingRequestDto");
        }
        return createRequestDto;
    }

    private Map<String, Object> createInitialBookingRequestDto() {
        Map<String, Object> dto = new HashMap<>();
        dto.put("userEmail", "john.doe@example.com");
        dto.put("startTime", "2023-08-01T10:00:00+00:00");
        dto.put("comments", "This is a side comment");
        return dto;
    }

    private void removeFieldIfExists(Map<String, Object> dto, String fieldToRemove) {
        if (!dto.containsKey(fieldToRemove)) {
            throw new IllegalArgumentException(
                    String.format("Field '%s' does not exist in BookingRequestDto and cannot be removed", fieldToRemove)
            );
        }
        log.info("Removing field '{}' from BookingRequestDto", fieldToRemove);
        dto.remove(fieldToRemove);
    }

    public void doAPIObjectCall(String urlEndpoint, HttpMethod httpMethod, Class<?> returnedTypeClass,
                                Map<String, Object> rawPayload, Map<String, String> queryParams) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        doFinalAPICall(urlEndpoint, httpMethod, headers, returnedTypeClass, rawPayload, queryParams);
    }

    public void doFinalAPICall(String endpoint, HttpMethod httpMethod, HttpHeaders headers, Class<?> returnedTypeClass, Map<String, Object> rawPayload, Map<String, String> queryParams) {
        log.info("Calling API endpoint: {}", endpoint);

        context.setHttpResponse(null);
        var apiEndpoint = replacePlaceholders(getApiUrl() + endpoint);

        try {
            // POST REQUESTS
            if (HttpMethod.POST.equals(httpMethod) && rawPayload != null) {
                // prepare object to be sent in request body
                var payload = cleanPayload(headers.getContentType(), rawPayload);
                var request = new HttpEntity<>(payload, headers);
                log.info("Endpoint '{}' called with POST method and with this payload {}", apiEndpoint, payload);

                final ResponseEntity<?> response = restTemplate.postForEntity(apiEndpoint, request, returnedTypeClass);
                context.setHttpResponse(response);
                logApiResponse();
            }

            // OTHER REQUEST METHODS
            else {
                log.info("Endpoint '{}' called with {} method", apiEndpoint, httpMethod);
                final var payload = objectMapper.writeValueAsString(rawPayload);
                log.info("Payload to be sent: {}", payload);
                var request = new HttpEntity<>(payload, headers);
                
                if (HttpMethod.PATCH.equals(httpMethod)){
                    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
                    requestFactory.setConnectTimeout(Duration.ofSeconds(1));
                    restTemplate.setRequestFactory(requestFactory);
                }
                final ResponseEntity<?> responseEntity;
                if (queryParams != null){
                    responseEntity = restTemplate.exchange(apiEndpoint, httpMethod, request, returnedTypeClass, queryParams);
                }
                else {
                    responseEntity = restTemplate.exchange(apiEndpoint, httpMethod, request, returnedTypeClass);
                }
                context.setHttpResponse(responseEntity);
                logApiResponse();
            }

        } catch (HttpClientErrorException e) {
            log.error("Error calling API endpoint: {}", apiEndpoint, e);
            context.setHttpResponseCode(e.getStatusCode().value());
            context.setException(e);
        } catch (Exception e) {
            if (context.getHttpResponse() != null) {
                context.setHttpResponseCode(context.getHttpResponse().getStatusCode().value());
            }
            log.error("Error calling API endpoint: {} with payload: {}", apiEndpoint, rawPayload != null ? rawPayload.toString() : "", e);
        }
    }

    private void logApiResponse() {
        log.info("API response: {}", context.getHttpResponse());
        context.setHttpResponseCode(context.getHttpResponse().getStatusCode().value());
        log.info("API response code: {}", context.getHttpResponseCode());
    }

    private String getApiUrl() {
        return apiHost + ":" + port + "/api/" + apiVersion + "/";
    }

    private Object cleanPayload(MediaType contentType, Map<String, Object> rawPayload) throws JsonProcessingException {
        final var BACKSLASH_PATTERN = "\\\\";
        final var OPENING_BRACKET_QUOTE_PATTERN = "\"\\[";
        final var CLOSING_BRACKET_QUOTE_PATTERN = "]\"";

        if (MediaType.APPLICATION_JSON.equals(contentType)) {
            var payload = objectMapper.writeValueAsString(rawPayload);
            payload = payload.replaceAll(BACKSLASH_PATTERN, "")
                    .replaceAll(OPENING_BRACKET_QUOTE_PATTERN, "[")
                    .replaceAll(CLOSING_BRACKET_QUOTE_PATTERN, "]");
            payload = replacePlaceholders(payload);
            return payload;
        }
        else {
            return toMultiValueMap(rawPayload.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> List.of(e.getValue()))));
        }
    }

    public String replacePlaceholders(String value) {
        // for  testing null values as input
        if (value == null || "null".equals(value)) {
            return null;
        }
        return value.replaceAll("@bookingId", String.valueOf(context.getActiveBookingId()));
    }

}
