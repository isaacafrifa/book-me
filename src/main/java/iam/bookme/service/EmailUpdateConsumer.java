package iam.bookme.service;

import iam.bookme.events.EmailUpdateEvent;
import iam.bookme.repository.BookingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailUpdateConsumer {
    @Autowired
    private BookingRepository bookingRepository;

    /*
     * Purpose:
     * - Consumes email update events and updates booking records
     * - Maintains email consistency across booking service
     * Behavior:
     * - Updates all bookings matching the old email
     * - Logs the update operation details
     * - Sends to DLQ after retry attempts if processing fails
     */
    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void consumeEmailUpdateEvent(EmailUpdateEvent event) {
        try {
            log.info("Received email update event - UserId: {}, Email change: {} -> {}",event.getUserId(),
                    event.getOldEmail(), event.getNewEmail());

//            int updatedCount = bookingRepository.updateUserEmail(event.getOldEmail(),event.getNewEmail());
//            log.info("Successfully updated {} booking(s) for userId: {}", updatedCount, event.getUserId());
            //TODO: This current event consuming implementation is just for demo purpose, hence there's no need to update emails in the bookingRepository.
            // Events may be valuable when Booking Statuses change eg. confirmed or cancelled bookings. Useful for: notifications, analytics, or audit logging

        } catch (Exception e) {
            log.error("Failed to process email update for userId: {}",
                    event.getUserId(), e);
            throw e; // Will trigger retry and eventually send to DLQ
        }
    }

}
