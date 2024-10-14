package cucumber.steps;

import iam.bookme.repository.BookingRepository;
import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@RequiredArgsConstructor
public class GeneralSteps {

    private final BookingRepository bookingRepository;

    @Given("there are {int} testing bookings in the database")
    public void thereAreTestingBookingsInTheDatabase(int expected) {
        long actual = bookingRepository.count();
        log.info("There are {} bookings in the database", actual);
        assertEquals(expected, actual);
    }
}
