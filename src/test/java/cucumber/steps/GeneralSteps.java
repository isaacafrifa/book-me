package cucumber.steps;

import cucumber.context.TestContext;
import cucumber.context.CucumberService;
import iam.bookme.dto.BookingDto;
import iam.bookme.repository.BookingRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@RequiredArgsConstructor
public class GeneralSteps {

    private final BookingRepository bookingRepository;
    private final CucumberService cucumberService;
    private final TestContext testContext;

    @Given("there are {int} test bookings in the database")
    public void thereAreTestingBookingsInTheDatabase(int expected) {
        long actual = bookingRepository.count();
        log.info("There are {} bookings in the database", actual);
        assertEquals(expected, actual);
    }

    @When("the endpoint {string} is called to get a booking")
    public void theEndpointIsCalled(String endpoint) {
        endpoint = cucumberService.replacePlaceholders(endpoint);
        cucumberService.doAPIObjectCall(endpoint, HttpMethod.GET, BookingDto.class, null,null);
        if (testContext.getHttpResponse() != null && testContext.getHttpResponse().getBody() != null) {
            testContext.setBookingDto((BookingDto) testContext.getHttpResponse().getBody());
        }
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

    @When("the endpoint {string} is called to get bookings")
    public void theEndpointIsCalledToGetBookings(String endpoint) {
        log.info("Getting all bookings with endpoint '{}'", endpoint);
        theEndpointIsCalled(endpoint);
    }
}
