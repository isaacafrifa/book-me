package iam.bookme.entity;

import iam.bookme.dto.BookingStatusDto;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID bookingId;

    private String userEmail;
    /*
     Using `@CreationTimestamp` and `@UpdateTimestamp` instead of `@CreatedDate` and `@LastModifiedDate`.
     While `@CreatedDate` and `@LastModifiedDate` typically expect data types like `LocalDateTime` or `Date`, they do not handle `OffsetDateTime` correctly due to the included offset.
    `@CreationTimestamp` and `@UpdateTimestamp` are specifically designed to manage timestamps, including those with time zones.
     */
    @CreationTimestamp
    private OffsetDateTime createdDate;
    @UpdateTimestamp
    private OffsetDateTime updatedDate;
    private OffsetDateTime startTime;
    private int durationInMinutes; // Duration of the booking in minutes

    @Enumerated(EnumType.STRING)
    private BookingStatusDto status;

    private String comments;

    @Version
    private Long version;

    // Additional methods for calculating endTime, validation, etc. (optional)
    @Transient
    public OffsetDateTime getEndTime() {
        return startTime.plusMinutes(durationInMinutes);
    }

    // Custom constructor excluding bookingId
    public Booking(String userEmail, OffsetDateTime createdDate, OffsetDateTime updatedDate, OffsetDateTime startTime, int durationInMinutes, BookingStatusDto status, String comments) {
        this.userEmail = userEmail;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.startTime = startTime;
        this.durationInMinutes = durationInMinutes;
        this.status = status;
        this.comments = comments;
    }

}
