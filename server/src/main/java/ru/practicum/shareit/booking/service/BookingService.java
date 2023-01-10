package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

public interface BookingService {

    BookingDto findBookingById(Long ownerId, Long bookingId);

    BookingDto addBooking(Long ownerId, BookingCreateDto bookingDto);

    BookingDto approveBooking(Long ownerId, Long bookingId, boolean isApproved);

    Page<BookingDto> getBookingForUserByState(Long ownerId, BookingState state, Integer from, Integer size);

    Page<BookingDto> getBookingForUserByItems(Long ownerId, BookingState state, Integer from, Integer size);

}
