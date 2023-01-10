package ru.practicum.shareit.item.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ItemUnavailableException extends RuntimeException {

    public ItemUnavailableException(final String message) {
        super(message);
    }

}
