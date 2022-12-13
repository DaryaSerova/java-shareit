package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto findBookingById(Long ownerId, Long bookingId);

    BookingDto addBooking(Long ownerId, BookingCreateDto bookingDto);

    BookingDto approveBooking(Long ownerId, Long bookingId, boolean isApproved);

    List<BookingDto> getBookingForUserByState(Long ownerId, BookingState state);

    List<BookingDto> getBookingForUserByItems(Long ownerId, BookingState state);

}
