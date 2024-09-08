package iam.bookme.repository;

import iam.bookme.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    // Find bookings starting after a specific time. It can be used to get upcoming bookings
    List<Booking> findAllByStartTimeAfter(OffsetDateTime specifiedTime);

    List<Booking> findAllByUserReferenceId(Long userId);
    // Booking exists for a specific user and time slot. It can be used to check for conflicts
    Optional<Booking> findByUserReferenceIdAndStartTime(Long userId, OffsetDateTime startTime);

}
