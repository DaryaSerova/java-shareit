package ru.practicum.shareit.item.comment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class CommentBadRequestException extends RuntimeException {

    public CommentBadRequestException(final String message) {
        super(message);
    }

}
