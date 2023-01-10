package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exceptions.*;
import ru.practicum.shareit.user.exceptions.UserInvalidEmailException;
import ru.practicum.shareit.item.comment.exception.CommentBadRequestException;
import ru.practicum.shareit.item.exceptions.ItemEmptyAvailableException;
import ru.practicum.shareit.item.exceptions.ItemEmptyNameException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.ItemUnavailableException;
import ru.practicum.shareit.request.exceptions.RequestEmptyNameException;
import ru.practicum.shareit.request.exceptions.RequestNotFoundException;
import ru.practicum.shareit.user.exceptions.UserEmptyEmailException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.exceptions.UserNotOwnerItemException;

@RestControllerAdvice("ru.practicum.shareit")
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class, UserNotFoundException.class,
    BookingNotFoundException.class, UserNotOwnerItemException.class, BookingOwnerNotFoundException.class,
    RequestNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final RuntimeException e) {
        log.info(e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({UserEmptyEmailException.class,
    UserInvalidEmailException.class, ItemEmptyAvailableException.class,
    ItemEmptyNameException.class, RequestEmptyNameException.class, BookingStateBadRequestException.class,
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
