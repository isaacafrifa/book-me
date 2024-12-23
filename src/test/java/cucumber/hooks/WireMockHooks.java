package cucumber.hooks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import iam.bookme.dto.UserDto;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;

import java.time.OffsetDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Scope(SCOPE_CUCUMBER_GLUE)
@Slf4j
public class WireMockHooks {
    private static final String USER_ENDPOINT = "/api/v1/users";
    private static final int WIREMOCK_PORT = 9060;
    private final WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().port(WIREMOCK_PORT));
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Before
    public void setUp() {
        wireMockServer.start();
        WireMock.configureFor("localhost", WIREMOCK_PORT);
        mockUserServiceCall();
        log.info("WireMock started on port: {}", WIREMOCK_PORT);
    }

    @After
    public void tearDown() {
        wireMockServer.resetAll();
        wireMockServer.stop();
    }

    private void mockUserServiceCall() {
        // Match the exact endpoint and query parameter pattern
        stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/v1/users/email"))
                .withQueryParam("userEmail", WireMock.equalTo("john.doe@example.com"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(buildUserServiceResponse("john.doe@example.com"))));

        // For POST requests
        stubFor(WireMock.post(WireMock.urlPathEqualTo("/api/v1/users"))
                .willReturn(WireMock.aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(buildUserServiceResponse("john.doe@example.com"))));

        log.info("Stubbed GET and POST requests for user endpoints");
    }

    @SneakyThrows
    private String buildUserServiceResponse(String email) {
        UserDto userDto = new UserDto(
                "John",
                "Doe",
                email,
                "1234567890",
                1L,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        return objectMapper.writeValueAsString(userDto);
    }
}
