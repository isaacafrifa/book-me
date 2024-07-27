package iam.bookme.service;

import iam.bookme.dto.BookingDto;
import iam.bookme.dto.BookingMapper;
import iam.bookme.exception.ResourceNotFoundException;
import iam.bookme.mapper.OrderByFieldMapper;
import iam.bookme.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    public static final String BOOKING_NOT_FOUND_MESSAGE = "Booking not found";
    public static final String BOOKING_ALREADY_EXISTS_MESSAGE = "Booking already exists";
    private static final String DEFAULT_ORDER_BY_FIELD = "bookingId";
    private final OrderByFieldMapper orderByFieldMapper = new OrderByFieldMapper();
    private final Logger log = LoggerFactory.getLogger(BookingService.class);

    public BookingService(BookingRepository bookingRepository, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        // map the user-provided field to db field
        orderByFieldMapper.setDefaultValue(DEFAULT_ORDER_BY_FIELD);
        orderByFieldMapper.addMapping("id", DEFAULT_ORDER_BY_FIELD);
        orderByFieldMapper.addMapping("startTime", "startTime");
        orderByFieldMapper.addMapping("createdOn", "createdDate");
        orderByFieldMapper.addMapping("status", "status");
        // orderByFieldMapper.addMapping("userId", "user.userId");
    }

    public Page<BookingDto> getAllBookings(int pageNo, int pageSize, String direction, String sortBy) {
        log.info("Get all bookings with pageNo '{}', pageSize '{}', direction '{}' and orderBy '{}'", pageNo, pageSize, direction, sortBy);

        var mappedSortByField = orderByFieldMapper.map(sortBy);
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(getSortDirection(direction), mappedSortByField));
        return bookingRepository.findAll(paging)
                .map(bookingMapper::toDto);
    }

    public BookingDto getBookingById(UUID bookingId) {
        log.info("Get booking by id '{}'", bookingId);

        return bookingRepository.findById(bookingId)
                .map(bookingMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(BOOKING_NOT_FOUND_MESSAGE));
    }
//
//    public BookingDto createBooking(BookingDto bookingDto) {
//        if (Boolean.TRUE.equals(bookingRepository.existsByUserEmailIgnoreCase(bookingDto.getUserEmail()))) {
//            log.info("Booking [user email: {}] already exists", bookingDto.getUserEmail());
//            throw new ResourceAlreadyExistsException(BOOKING_ALREADY_EXISTS_MESSAGE);
//        }
//        Booking toBeSaved = bookingMapper.toEntity(bookingDto);
//        return bookingMapper.toDto(bookingRepository.save(toBeSaved));
//    }
//
//    public BookingDto updateBooking(UUID bookingId, BookingDto bookingDto) {
//        var existingBooking = getExistingBooking(bookingId);
//
//        existingBooking.setUserEmail(bookingDto.getUserEmail());
//        existingBooking.setStartTime(bookingDto.getStartTime());
//        // duration isn't updated
//        existingBooking.setStatus(bookingDto.getStatus());
//        existingBooking.setComments(bookingDto.getComments());
//        return bookingMapper.toDto(bookingRepository.save(existingBooking));
//    }
//
//    public void deleteBooking(UUID bookingId) {
//        if (!bookingRepository.existsById(bookingId)) {
//            log.info("Booking with id {} not found", bookingId);
//            throw new ResourceNotFoundException(BOOKING_NOT_FOUND_MESSAGE);
//        }
//        bookingRepository.deleteById(bookingId);
//        log.info("Booking with id {} deleted successfully", bookingId);
//    }
//
//
//    private Booking getExistingBooking(UUID bookingId) {
//        return bookingRepository
//                .findById(bookingId).
//                orElseThrow(() -> {
//                    log.info("Booking with id {} not found", bookingId);
//                    return new ResourceNotFoundException(BOOKING_NOT_FOUND_MESSAGE);
//                });
//    }

    private Sort.Direction getSortDirection(String direction) {
        assert direction != null;
        if (direction.contains("desc")) return Sort.Direction.DESC;
        return Sort.Direction.ASC;
    }
}
