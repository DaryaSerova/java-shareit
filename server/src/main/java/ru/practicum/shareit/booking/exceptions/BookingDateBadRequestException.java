package ru.practicum.shareit.booking.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BookingDateBadRequestException extends RuntimeException {

    public BookingDateBadRequestException(final String message) {
        super(message);
    }

}
