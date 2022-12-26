package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exceptions.BookingStateBadRequestException;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    public static final String OWNER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader(OWNER_ID) Long ownerId,
                                 @RequestBody BookingCreateDto bookingDto) {
        log.info("запрос на добавление бронирования: " + bookingDto);
        return bookingService.addBooking(ownerId, bookingDto);
    }

    @RequestMapping(value = "/{bookingId}",
            method = RequestMethod.PATCH)
    public BookingDto approveBooking(@RequestHeader(OWNER_ID) Long ownerId,
                                     @PathVariable("bookingId") Long bookingId,
                                     @RequestParam("approved") Boolean isApproved) {
        log.info("Подтверждение или отклонение запроса на бронирование:" + bookingId + "с признаком " + isApproved);
        return bookingService.approveBooking(ownerId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(OWNER_ID) Long ownerId,
                                 @PathVariable("bookingId") Long bookingId) {
        log.info("запрос на получение бронирования " + bookingId);
        return bookingService.findBookingById(ownerId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingForUserByState(
            @RequestHeader(OWNER_ID) Long ownerId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "20") @PositiveOrZero Integer size) {

        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (Exception e) {
            throw new BookingStateBadRequestException(String.format("Unknown state: %s", state));
        }
        log.info("запрос на получение бронирования пользователя:" + ownerId);
        return bookingService.getBookingForUserByState(ownerId, bookingState, from, size).getContent();
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingForUserByItems(
            @RequestHeader(OWNER_ID) Long ownerId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "20") @PositiveOrZero Integer size) {

        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (Exception e) {
            throw new BookingStateBadRequestException(String.format("Unknown state: %s", state));
        }
        log.info("запрос на получение бронирования пользователя:" + ownerId);
        return bookingService.getBookingForUserByItems(ownerId, bookingState, from, size).getContent();
    }
}
