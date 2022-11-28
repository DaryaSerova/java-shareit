package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ItemEmptyDescriptionException extends RuntimeException {

    public ItemEmptyDescriptionException(final String message) {
        super(message);
    }

}
