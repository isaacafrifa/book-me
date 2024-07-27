package iam.bookme.controller;

import iam.bookme.dto.BookingsListDto;
import iam.bookme.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class BookingController implements BookingsApi {

    private final BookingService bookingService;
    private final Logger log = LoggerFactory.getLogger(BookingController.class);

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }


    @Override
    public ResponseEntity<BookingsListDto> getBookings(Integer pageNo, Integer pageSize, String direction, String orderBy) {
        log.debug("Received request to get all bookings with pageNo {}, pageSize {}, direction {} and orderBy {}", pageNo, pageSize, direction, orderBy);
        var allBookings = bookingService.getAllBookings(pageNo, pageSize, direction, orderBy);
        var response = new BookingsListDto();
        response.setContent(allBookings.getContent());
        response.setTotalElements(allBookings.getTotalElements());
        response.setTotalPages(allBookings.getTotalPages());
        return ResponseEntity.ok(response);
    }
}
