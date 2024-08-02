package iam.bookme.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookingOptimisticLockException extends RuntimeException {
    public BookingOptimisticLockException(String message) {
        super(message);
    }
}
