package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UserDuplicateEmailException extends RuntimeException {

    public UserDuplicateEmailException(final String message) {
        super(message);
    }

}
