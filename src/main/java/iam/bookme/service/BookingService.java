package iam.bookme.service;

import iam.bookme.dto.BookingDto;
import iam.bookme.dto.BookingMapper;
import iam.bookme.entity.Booking;
import iam.bookme.repository.BookingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public record BookingService(BookingRepository bookingRepository, BookingMapper bookingMapper) {

    public static final String BOOKING_NOT_FOUND_MESSAGE = "Booking not found";

    public Page<BookingDto> getAllBookings(int pageNo, int pageSize, String direction, String sortBy) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(getSortDirection(direction), sortBy));
        return bookingRepository.findAll(paging)
                .map(bookingMapper::toDto);
    }

    public BookingDto getBookingById(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .map(bookingMapper::toDto)
                .orElseThrow(() -> new RuntimeException(BOOKING_NOT_FOUND_MESSAGE));
    }

    public BookingDto createBooking(BookingDto bookingDto) {
        if (Boolean.TRUE.equals(bookingRepository.existsByUserEmailIgnoreCase(bookingDto.getUserEmail()))) {
            log.info("Booking [user email: {}] already exists", bookingDto.getUserEmail());
            throw new RuntimeException("This booking already exists");
        }
        Booking toBeSaved = bookingMapper.toEntity(bookingDto);
        return bookingMapper.toDto(bookingRepository.save(toBeSaved));
    }

    public BookingDto updateBooking(UUID bookingId, BookingDto bookingDto) {
        var existingBooking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException(BOOKING_NOT_FOUND_MESSAGE));

        existingBooking.setUserEmail(bookingDto.getUserEmail());
        existingBooking.setStartTime(bookingDto.getStartTime());
        // duration isn't updated
        existingBooking.setStatus(bookingDto.getStatus());
        existingBooking.setComments(bookingDto.getComments());
        return bookingMapper.toDto(bookingRepository.save(existingBooking));
    }

    public void deleteBooking(UUID bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            log.info("Booking with id {} not found", bookingId);
            throw new RuntimeException(BOOKING_NOT_FOUND_MESSAGE);
        }
        bookingRepository.deleteById(bookingId);
        log.info("Booking with id {} deleted successfully", bookingId);
    }

    private Sort.Direction getSortDirection(String direction) {
        assert direction != null;
        if (direction.contains("desc")) return Sort.Direction.DESC;
        return Sort.Direction.ASC;
    }
}
