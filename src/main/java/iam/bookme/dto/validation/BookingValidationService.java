package iam.bookme.dto.validation;

import iam.bookme.dto.BookingRequestDto;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class BookingValidationService {

    public void validateBookingRequestDto(BookingRequestDto bookingRequestDto) {
        final var EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Assert.notNull(bookingRequestDto.getUserEmail(), "Email address cannot be null");
        Assert.isTrue(bookingRequestDto.getUserEmail().matches(EMAIL_PATTERN), "Invalid email address format");
        // Add other validation rules as needed
    }
}
