package cucumber.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import iam.bookme.repository.BookingRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Slf4j
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class UtilCucumber {

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


    public void doAPIObjectCall(String urlEndpoint, HttpMethod httpMethod, Class<?> returnedTypeClass, Object rawPayload) {
        Map<String, Object> payload = objectMapper.convertValue(rawPayload, Map.class);
        doFinalAPICall(urlEndpoint, httpMethod, returnedTypeClass, payload);
    }

    @Transactional
    public void doFinalAPICall(String endpoint, HttpMethod httpMethod, Class<?> returnedTypeClass, Map<String, Object> rawPayload) {
        log.info("Calling API endpoint: {}", endpoint);

        context.setHttpResponse(null);
        HttpHeaders  headers = new HttpHeaders();
        var apiEndpoint = getApiUrl() + endpoint;

        if (HttpMethod.GET.equals(httpMethod) && rawPayload!= null) {
//            context.setHttpResponse(restTemplate.exchange(apiEndpoint, httpMethod, null, returnedTypeClass));
        } else {
//            context.setHttpResponse(restTemplate.exchange(apiEndpoint, httpMethod, new HttpEntity<>(payload, headers), returnedTypeClass));
        }
    }

    private String getApiUrl() {
        StringBuilder apiBase = new StringBuilder(apiHost);
        apiBase.append(":").append(port).append("/api/").append(apiVersion).append("/");
        return apiBase.toString();
    }

}
