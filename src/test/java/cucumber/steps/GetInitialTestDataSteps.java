package cucumber.steps;

import cucumber.models.BookingTestData;
import iam.bookme.dto.BookingDto;
import iam.bookme.dto.BookingMapper;
import iam.bookme.dto.BookingStatusDto;
import iam.bookme.repository.BookingRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@RequiredArgsConstructor
public class GetInitialTestDataSteps {

    private final BookingRepository bookingRepository;
    private List<BookingDto> retrievedBookings;
    private final BookingMapper bookingMapper;

    @Given("the test database is initialized with test data")
    public void testDatabaseIsInitialized() {
        // TestContainers should have already initialized the database
        assertNotNull(bookingRepository);
    }

    @When("I retrieve all bookings from the database")
    public void retrieveAllBookings() {
        retrievedBookings = bookingRepository.findAll().stream()
                .map(bookingMapper::toDto).toList();
    }

    @Then("the total number of bookings should be {int}")
    public void verifyBookingCount(int expectedCount) {
        assertEquals(expectedCount, bookingRepository.count());
    }

    @Then("the bookings should contain the following data:")
    public void verifyBookingsData(DataTable dataTable) {
        log.info("Verifying the response body contains the following booking details: \n{}", dataTable);

        List<Map<String, String>> expected = dataTable.asMaps();
        var expectedBookings = expected.stream()
                .map(map -> new BookingTestData(
                        Long.parseLong(map.get("bookingId")),
                        Long.parseLong(map.get("userId")),
                        OffsetDateTime.parse(map.get("startTime")),
                        BookingStatusDto.valueOf(map.get("bookingStatus").toUpperCase()),
                        map.get("comments")
                ))
                .toList();

        var actualBookings = retrievedBookings.stream()
                .map(booking -> new BookingTestData(
                        booking.getBookingId(),
                        booking.getUserId(),
                        booking.getStartTime(),
                        booking.getBookingStatus(),
                        booking.getComments()
                ))
                .toList();
        assertEquals(expectedBookings, actualBookings,
                "The actual bookings should match the expected bookings");
    }

}
