package cucumber.steps;

import cucumber.context.TestContext;
import iam.bookme.repository.BookingRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

}
