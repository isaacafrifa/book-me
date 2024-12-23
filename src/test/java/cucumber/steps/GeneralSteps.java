package cucumber.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.context.TestContext;
import iam.bookme.repository.BookingRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
@RequiredArgsConstructor
public class GeneralSteps {

    private final BookingRepository bookingRepository;
    private final TestContext testContext;

    @Given("there are {int} test bookings in the database")
    public void thereAreTestingBookingsInTheDatabase(int expected) {
        long actual = bookingRepository.count();
        log.info("There are {} bookings in the database", actual);
        assertEquals(expected, actual);
        testContext.setInitialBookingsCountInDatabase( (int) actual);
    }

    @Then("the response status code {int} should be returned")
    public void theResponseStatusCodeShouldBeReturned(int expected) {
        log.info("Expected HTTP status code should be {}", expected);

        if (testContext.getHttpResponseCode() == -1){
            log.info("No response received");
        }
        int actual = testContext.getHttpResponseCode();
        assertEquals(expected, actual, "HTTP status code do not match");
    }

    @And("the response should contain the error message {string}")
    public void theResponseShouldContainTheMessage(String expected) {
        log.info("Expected response message should be {}", expected);

        final var exception = testContext.getException();
        assertNotNull(exception);

        // Extract the JSON part from the exception message
        String jsonPart = exception.getMessage()
                .substring(exception.getMessage().indexOf("{")); // Get JSON part from error response (removes "400 : " prefix)
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonPart);
            if (!jsonNode.has("message")) {
                fail("Expected 'message' field is missing in JSON response");
            }
            assert jsonNode.get("message").asText() != null : "Message content is null";
            String actual = jsonNode.get("message").asText();
            // Split messages and sort them for comparison
            List<String> expectedMessages = Arrays.stream(expected.split(","))
                    .map(String::trim)
                    .sorted()
                    .collect(Collectors.toList());

            List<String> actualMessages = Arrays.stream(actual.split(","))
                    .map(String::trim)
                    .sorted()
                    .collect(Collectors.toList());

            assertEquals(expectedMessages, actualMessages, "Error messages do not match");

        } catch (JsonProcessingException e) {
            fail("Failed to parse error message JSON: " + e.getMessage());
        }
    }


}
