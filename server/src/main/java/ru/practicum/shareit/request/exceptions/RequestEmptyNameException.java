package ru.practicum.shareit.request.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class RequestEmptyNameException extends RuntimeException {

    public RequestEmptyNameException(final String message) {
        super(message);
    }

}
