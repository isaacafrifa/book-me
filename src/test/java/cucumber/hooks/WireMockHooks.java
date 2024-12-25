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
import java.util.Arrays;
import java.util.List;

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

    private record UserEmailData(String firstName, String lastName, String email) {}

    private static final List<UserEmailData> USER_EMAIL_DATA = Arrays.asList(
            new UserEmailData("John", "Doe", "john.doe@example.com"),
            new UserEmailData("Janet", "Jackson", "janet.jackson@test.com")
    );

    private void mockUserServiceCall() {
        // Set up stubs for each user
        for (UserEmailData userData : USER_EMAIL_DATA) {
            // Match the exact endpoint and query parameter pattern for each email
            stubFor(WireMock.get(WireMock.urlPathEqualTo("/api/v1/users/email"))
                    .withQueryParam("userEmail", WireMock.equalTo(userData.email()))
                    .willReturn(WireMock.aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(buildUserServiceResponse(userData.email()))));

            // For POST requests
            stubFor(WireMock.post(WireMock.urlPathEqualTo("/api/v1/users"))
                    .willReturn(WireMock.aResponse()
                            .withStatus(201)
                            .withHeader("Content-Type", "application/json")
                            .withBody(buildUserServiceResponse(userData.email()))));
        }

        log.info("Stubbed GET and POST requests for user endpoints");
    }

    @SneakyThrows
    private String buildUserServiceResponse(String email) {
        UserDto userDto = switch (email) {
            case "john.doe@example.com" -> new UserDto(
                    "John",
                    "Doe",
                    email,
                    "1234567890",
                    1L,
                    OffsetDateTime.now(),
                    OffsetDateTime.now()
            );
            case "janet.jackson@test.com" -> new UserDto(
                    "Janet",
                    "Jackson",
                    email,
                    "9876543210",
                    2L,
                    OffsetDateTime.now(),
                    OffsetDateTime.now()
            );
            default -> throw new IllegalArgumentException("Unexpected email: " + email);
        };

        return objectMapper.writeValueAsString(userDto);
    }

}
