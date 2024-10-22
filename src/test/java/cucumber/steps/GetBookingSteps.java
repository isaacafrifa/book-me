package cucumber.steps;

import cucumber.context.TestContext;
import iam.bookme.entity.Booking;
import iam.bookme.repository.BookingRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@RequiredArgsConstructor
public class GetBookingSteps {

    private final BookingRepository bookingRepository;
    private final TestContext testContext;

    @Given("a booking exists with ID {int} in the database")
    public void aBookingExistsWithID(int arg0) {
        var foundBooking = bookingRepository.findById((long) arg0);
        assert foundBooking.isPresent();
        log.info("Found booking: {}", foundBooking.get());
    }

    @And("the response body should contain the bookingID {int}")
    public void theResponseBodyShouldContainTheBookingID(int expectedId) {
        log.info("The response body should contain expected booking ID: {}", expectedId);

        final var bookingDto = testContext.getBookingDto();
        var actualId = bookingDto.getBookingId();
        assertEquals(expectedId, actualId, "Booking IDs do not match");
    }

    @Given("no booking exists with id {int}")
    public void noBookingExistsWithId(int arg0) {
        log.info("Verify that the booking ID '{}' does not exist in the database", arg0);
        Long id = (long) arg0;
        Optional<Booking> booking = bookingRepository.findById(id);
        assertTrue(booking.isEmpty());

    }

    @And("the response body should contain the message {string}")
    public void theResponseBodyShouldContainTheMessage(String expected) {
        log.info("the response body contains '{}'", expected);
        final var actual = testContext.getException().getMessage();
        assertTrue(actual.contains(expected));
    }
}
