package ru.practicum.shareit.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class UserNotOwnerItemException extends RuntimeException {

    public UserNotOwnerItemException(final String message) {
        super(message);
    }

}
