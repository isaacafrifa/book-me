package cucumber.hooks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import iam.bookme.dto.UserDto;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Scope;

import java.time.OffsetDateTime;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Scope(SCOPE_CUCUMBER_GLUE)
public class WireMockHooks {
    private static final String USER_ENDPOINT = "/api/v1/users";
    private static final int WIREMOCK_PORT = 9090;
    private final WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().port(WIREMOCK_PORT));
    private final ObjectMapper objectMapper= new ObjectMapper();

    @Before
    public void setUp() {
        WireMock.configureFor(WIREMOCK_PORT);
        wireMockServer.start();
       mockUserServiceCall();
    }

    @After
    public void tearDown() {
        wireMockServer.stop();
    }

    private void mockUserServiceCall() {
        var userEmails = List.of("john.doe@example.com", "notfound@example.com");
        for (String userEmail : userEmails) {
          if (userEmail.equals("john.doe@example.com")){
              stubFor(get(USER_ENDPOINT.concat("/email"))
                      .willReturn(okJson(
                              buildUserServiceResponse(userEmail))));
          }
          else {
              stubFor(get(USER_ENDPOINT.concat("/email"))
                      .willReturn(notFound()));
          }
        }

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
