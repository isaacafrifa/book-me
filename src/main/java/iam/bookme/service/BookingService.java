package iam.bookme.service;

import iam.bookme.dto.BookingDto;
import iam.bookme.dto.BookingMapper;
import iam.bookme.entity.Booking;
import iam.bookme.repository.BookingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record BookingService (BookingRepository bookingRepository, BookingMapper  bookingMapper) {


    public Page<BookingDto> getAllBookings(int pageNo, int pageSize, String direction, String sortBy) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(getSortDirection(direction),sortBy));
        return bookingRepository.findAll(paging)
                .map(bookingMapper::toDto);
    }


    private Sort.Direction getSortDirection(String direction) {
        assert direction != null;
        if (direction.contains("desc")) return Sort.Direction.DESC;
        return Sort.Direction.ASC;
    }

    public BookingDto createBooking(BookingDto bookingDto) {
        Booking toBeSaved = bookingMapper.toEntity(bookingDto);
        return bookingMapper.toDto(bookingRepository.save(toBeSaved));
    }

}
