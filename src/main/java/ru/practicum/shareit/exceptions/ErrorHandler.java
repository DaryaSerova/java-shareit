package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exceptions.*;
import ru.practicum.shareit.item.comment.exception.CommentBadRequestException;
import ru.practicum.shareit.item.exceptions.*;
import ru.practicum.shareit.user.exceptions.*;

@RestControllerAdvice("ru.practicum.shareit")
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class,
            UserNotFoundException.class, BookingNotFoundException.class,
            UserNotOwnerItemException.class, BookingOwnerNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final RuntimeException e) {
        log.info(e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({UserDuplicateEmailException.class,
            UserEmptyEmailException.class, UserInvalidEmailException.class,
            ItemEmptyAvailableException.class,
            ItemEmptyDescriptionException.class, ItemEmptyNameException.class,
            BookingStateBadRequestException.class,
            BookingStatusBadRequestException.class, BookingDateBadRequestException.class,
            ItemUnavailableException.class, CommentBadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestFoundException(final RuntimeException e) {
        log.info(e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.info(e.getMessage(), e);
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}
