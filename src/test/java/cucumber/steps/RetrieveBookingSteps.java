package cucumber.steps;

import iam.bookme.repository.BookingRepository;
import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RetrieveBookingSteps {

    private final BookingRepository bookingRepository;

    @Given("a booking exists with ID {int} in the database")
    public void aBookingExistsWithID(int arg0) {
        var foundBooking = bookingRepository.findById((long) arg0);
        assert foundBooking.isPresent();
        log.info("Found booking: {}", foundBooking.get());
    }
}
