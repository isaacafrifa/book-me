package iam.bookme.controller;

import iam.bookme.dto.BookingDto;
import iam.bookme.dto.BookingRequestDto;
import iam.bookme.dto.BookingsListDto;
import iam.bookme.service.BookingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class BookingController implements BookingsApi {

    private final BookingService bookingService;

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

    @Override
    public ResponseEntity<BookingDto> getBookingById(UUID id) {
        log.debug("Received request to get booking by id {}", id);
        var response = bookingService.getBookingById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<BookingDto> createBooking(@Valid BookingRequestDto bookingRequestDto) {
        log.debug("Received request to create booking {}", bookingRequestDto);
        var response = bookingService.createBooking(bookingRequestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<BookingDto> updateBooking(UUID id, @Valid BookingRequestDto bookingRequestDto) {
        log.debug("Received request to update booking with id {}", id);
        var response = bookingService.updateBooking(id, bookingRequestDto);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteBooking(UUID id) {
        log.debug("Received request to delete booking with id {}", id);
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

}
