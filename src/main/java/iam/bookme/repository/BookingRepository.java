package iam.bookme.repository;

import iam.bookme.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    Optional<Booking> findByBookingId(UUID bookingId);

    // Find bookings starting after a specific time. It can be used to get upcoming bookings
    List<Booking> findAllByStartTimeAfter(LocalDateTime specifiedTime);

    List<Booking> findAllByUserEmail(String userEmail);
}
