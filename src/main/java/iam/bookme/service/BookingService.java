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
public record BookingService (BookingRepository bookingRepository, BookingMapper  bookingMapper) {


    public Page<BookingDto> getAllBookings(int pageNo, int pageSize, String direction, String sortBy) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(getSortDirection(direction),sortBy));
        return bookingRepository.findAll(paging)
                .map(bookingMapper::toDto);
    }

    public BookingDto getBookingById(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .map(bookingMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public BookingDto createBooking(BookingDto bookingDto) {
        if (Boolean.TRUE.equals(bookingRepository.existsByUserEmailIgnoreCase(bookingDto.getUserEmail()))){
            log.info("Booking [user email: {}] already exists", bookingDto.getUserEmail());
            throw new RuntimeException("This booking already exists");
        }
        Booking toBeSaved = bookingMapper.toEntity(bookingDto);
        return bookingMapper.toDto(bookingRepository.save(toBeSaved));
    }


    private Sort.Direction getSortDirection(String direction) {
        assert direction != null;
        if (direction.contains("desc")) return Sort.Direction.DESC;
        return Sort.Direction.ASC;
    }
}
