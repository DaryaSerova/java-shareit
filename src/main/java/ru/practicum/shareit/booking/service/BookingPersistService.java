package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingPersistService {

    Booking createBooking(Booking booking);

    Booking updateBooking(Booking booking);

    Optional<Booking> findBookingById(Long id);

    List<Booking> findBookingByItemId(Long itemId);

    List<Booking> findBookingByItemIdAndStatusNotInAndStartBefore(
                  Long itemId, List<BookingStatus> statuses, LocalDateTime start);

    Page<Booking> findBookingForUserByState(Long ownerId, BookingState state, Integer from, Integer size);

    Page<Booking> findBookingForUserByItems(Long ownerId, List<Long> itemIds, BookingState state,
                                            Integer from, Integer size);
}
