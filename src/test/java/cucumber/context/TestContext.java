package cucumber.context;

import iam.bookme.dto.BookingDto;
import iam.bookme.dto.BookingRequestDto;
import iam.bookme.dto.BookingsListDto;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

/*
 * Class to store values that can be shared between steps
 */
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
@Data
@ActiveProfiles("cucumber")
public class TestContext {
    private ResponseEntity<?> httpResponse;
    private int httpResponseCode = -1;
    private List<Long> bookingsToDelete;
    private BookingDto bookingDto;
    private BookingRequestDto bookingRequestDto;
    private Long activeBookingId;
    private BookingsListDto bookingsListDto;
    private HttpClientErrorException exception;
    private int initialBookingsCountInDatabase;
}
