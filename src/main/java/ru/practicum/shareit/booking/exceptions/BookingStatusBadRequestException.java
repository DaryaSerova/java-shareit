package ru.practicum.shareit.booking.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BookingStatusBadRequestException extends RuntimeException {

    public BookingStatusBadRequestException(final String message) {
        super(message);
    }

}
