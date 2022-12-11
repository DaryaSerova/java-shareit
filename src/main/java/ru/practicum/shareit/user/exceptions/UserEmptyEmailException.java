package ru.practicum.shareit.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UserEmptyEmailException extends RuntimeException {

    public UserEmptyEmailException(final String message) {
        super(message);
    }

}
