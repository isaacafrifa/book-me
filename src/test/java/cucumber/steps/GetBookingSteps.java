package cucumber.steps;

import cucumber.context.CucumberService;
import cucumber.context.TestContext;
import iam.bookme.dto.BookingDto;
import iam.bookme.dto.BookingsListDto;
import iam.bookme.entity.Booking;
import iam.bookme.repository.BookingRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@RequiredArgsConstructor
public class GetBookingSteps {

    public static final String CREATED_ON = "createdOn";
    public static final String STATUS = "status";
    public static final String START_TIME = "startTime";
    public static final String ID = "id";
    private final BookingRepository bookingRepository;
    private final TestContext testContext;
    private final CucumberService cucumberService;

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

    @When("the endpoint {string} is called to get a single booking")
    public void theGetSingleBookingEndpointIsCalled(String endpoint) {
        endpoint = cucumberService.replacePlaceholders(endpoint);
        cucumberService.doAPIObjectCall(endpoint, HttpMethod.GET, BookingDto.class, null, null);
        if (testContext.getHttpResponse() != null && testContext.getHttpResponse().getBody() != null) {
            testContext.setBookingDto((BookingDto) testContext.getHttpResponse().getBody());
        }
    }

    @When("the endpoint {string} is called to get bookings")
    public void theEndpointIsCalledToGetBookings(String endpoint) {
        log.info("Getting all bookings with endpoint '{}'", endpoint);
        cucumberService.doAPIObjectCall(endpoint, HttpMethod.GET, BookingsListDto.class, null, null);

        Optional.ofNullable(testContext.getHttpResponse())
                .map(responseEntity -> testContext.getHttpResponse().getBody())
                .filter(BookingsListDto.class::isInstance)
                .map(BookingsListDto.class::cast)
                .ifPresent(bookingsListDto -> {
                    log.info("Received {} bookings", bookingsListDto.getContent().size());
                    testContext.setBookingsListDto(bookingsListDto);
                });
    }

    @And("{int} bookings should be returned")
    public void bookingsShouldBeReturned(int expected) {
        assertNotNull(testContext.getBookingsListDto());
        var actualCount = testContext.getBookingsListDto().getContent().size();
        assertEquals(expected, actualCount);
    }

    @And("the bookings should be sorted by {string} in {string} order")
    public void theBookingsShouldBeSortedByInOrder(String orderField, String sortDirection) {
    log.info("Verifying the orderField: '{}' and orderDirection: '{}'", orderField, sortDirection);

        var actualContent = testContext.getBookingsListDto().getContent();
        assertNotNull(actualContent);

        // Verify the sorting is correct
        boolean isSorted = sortDirection.equalsIgnoreCase("asc") ?
                isSortedAscending(actualContent, orderField) :
                isSortedDescending(actualContent, orderField);

        assertTrue(isSorted, "Bookings are not correctly sorted");
    }

    private boolean isSortedDescending(List<BookingDto> actualContent, String orderField) {
        // check for Empty or single-element lists
        if (actualContent.size() <= 1) {
            return true;  // Empty or single-element lists are always sorted
        }

        for (int i = 0; i < actualContent.size() - 1; i++) {
            BookingDto current = actualContent.get(i);
            BookingDto next = actualContent.get(i + 1);
            switch (orderField) {
                case ID:
                    if (current.getBookingId() < next.getBookingId()) {
                        return false;
                    }
                    break;
                case START_TIME:
                    if (current.getStartTime().isBefore(next.getStartTime())) {
                        return false;
                    }
                    break;
                case STATUS:
                    if (current.getBookingStatus().getValue().compareTo(next.getBookingStatus().getValue()) < 0) {
                        return false;
                    }
                    break;
                case CREATED_ON:
                    if (current.getCreatedDate().isBefore(next.getCreatedDate())) {
                        return false;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported order field: " + orderField);
            }
        }
        return true;
    }

    // using the Comparator class approach
    private boolean isSortedAscending(List<BookingDto> actualContent, String orderField) {
        // check for Empty or single-element lists
        if (actualContent.size() <= 1) {
            return true;  // Empty or single-element lists are always sorted
        }

        Comparator<BookingDto> comparator = switch (orderField) {
            case ID -> Comparator.comparing(BookingDto::getBookingId);
            case START_TIME -> Comparator.comparing(BookingDto::getStartTime);
            case STATUS -> Comparator.comparing(booking -> booking.getBookingStatus().getValue());
            case CREATED_ON -> Comparator.comparing(BookingDto::getCreatedDate);
            default -> throw new IllegalArgumentException("Unsupported order field: " + orderField);
        };

        for (int i = 0; i < actualContent.size() - 1; i++) {
            if (comparator.compare(actualContent.get(i), actualContent.get(i + 1)) > 0) {
                return false;
            }
        }
        return true;
    }


}