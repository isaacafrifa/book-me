package cucumber.steps;

import cucumber.context.TestContext;
import cucumber.context.UtilCucumber;
import iam.bookme.dto.BookingDto;
import iam.bookme.repository.BookingRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@RequiredArgsConstructor
public class GeneralSteps {

    private final BookingRepository bookingRepository;
    private final UtilCucumber utilCucumber;
    private final TestContext testContext;

    @Given("there are {int} testing bookings in the database")
    public void thereAreTestingBookingsInTheDatabase(int expected) {
        long actual = bookingRepository.count();
        log.info("There are {} bookings in the database", actual);
        assertEquals(expected, actual);
    }

    @When("the endpoint {string} is called to get a booking")
    public void theEndpointIsCalled(String endpoint) {
        endpoint = utilCucumber.replacePlaceholders(endpoint);
        utilCucumber.doAPIObjectCall(endpoint, HttpMethod.GET, BookingDto.class, null,null);
        if (testContext.getHttpResponse() != null && testContext.getHttpResponse().getBody() != null) {
            testContext.setBookingDto((BookingDto) testContext.getHttpResponse().getBody());
        } else {
            log.info("No response received");
        }
    }
}
