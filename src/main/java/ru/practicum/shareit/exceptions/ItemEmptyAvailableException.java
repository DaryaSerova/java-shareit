package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ItemEmptyAvailableException extends RuntimeException {

    public ItemEmptyAvailableException(final String message) {
        super(message);
    }

}
