package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.exceptions.BookingStateBadRequestException;

import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    public static final String OWNER_ID = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(OWNER_ID) Long ownerId,
                                             @RequestBody BookingCreateDto bookingDto) {
        log.info("запрос на добавление бронирования: " + bookingDto);
        return bookingClient.addBooking(ownerId, bookingDto);
    }

    @RequestMapping(value = "/{bookingId}",
            method = RequestMethod.PATCH)
    public ResponseEntity<Object> approveBooking(@RequestHeader(OWNER_ID) Long ownerId,
                                                 @PathVariable("bookingId") Long bookingId,
                                                 @RequestParam("approved") Boolean isApproved) {
        log.info("Подтверждение или отклонение запроса на бронирование:" + bookingId + "с признаком " + isApproved);
        return bookingClient.approveBooking(ownerId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(OWNER_ID) Long ownerId,
                                             @PathVariable("bookingId") Long bookingId) {
        log.info("запрос на получение бронирования " + bookingId);
        return bookingClient.getBooking(ownerId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingForUserByState(
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
        return bookingClient.getBookings(ownerId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingForUserByItems(
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
        return bookingClient.getBookingsByOwner(ownerId, state, from, size);
    }
}
