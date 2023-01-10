package ru.practicum.shareit.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UserInvalidEmailException extends RuntimeException {

    public UserInvalidEmailException(final String message) {
        super(message);
    }

}
