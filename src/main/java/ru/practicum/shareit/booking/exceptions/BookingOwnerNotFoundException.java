package ru.practicum.shareit.booking.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class BookingOwnerNotFoundException extends RuntimeException {

    public BookingOwnerNotFoundException(final String message) {
        super(message);
    }

}
