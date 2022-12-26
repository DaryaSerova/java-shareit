package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingPersistServiceImpl implements BookingPersistService {

    private final BookingRepository bookingRepository;

    @Override
    public Booking createBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public Optional<Booking> findBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    @Override
    public List<Booking> findBookingByItemId(Long itemId) {
        return bookingRepository.findBookingByItemId(itemId);
    }

    @Override
    public List<Booking> findBookingByItemIdAndStatusNotInAndStartBefore(
                         Long itemId, List<BookingStatus> statuses, LocalDateTime start) {
        return bookingRepository.findBookingByItemIdAndStatusNotInAndStartBefore(itemId, statuses, start);
    }

    @Override
    public Page<Booking> findBookingForUserByState(Long ownerId, BookingState state, Integer from, Integer size) {

        var page = PageRequest.of(from / size, size, Sort.by("start").descending());

        switch (state) {
            case CURRENT:
                return bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(
                        ownerId, LocalDateTime.now(), LocalDateTime.now(), page);
            case FUTURE:
                return bookingRepository.findByBookerIdAndStartAfter(ownerId, LocalDateTime.now(), page);
            case PAST:
                return bookingRepository.findByBookerIdAndStartBeforeAndEndBefore(
                        ownerId, LocalDateTime.now(), LocalDateTime.now(), page);
            case WAITING:
                return bookingRepository.findByBookerIdAndStatus(ownerId, BookingStatus.WAITING, page);
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatus(ownerId, BookingStatus.REJECTED, page);
            default:
                return bookingRepository.findByBookerId(ownerId, page);
        }
    }

    @Override
    public Page<Booking> findBookingForUserByItems(Long ownerId, List<Long> itemIds, BookingState state,
                                                   Integer from, Integer size) {

        var page = PageRequest.of(from, size, Sort.by("start").descending());

        switch (state) {
            case CURRENT:
                return bookingRepository.findByStartBeforeAndEndAfterAndItemIdIn(
                        LocalDateTime.now(), LocalDateTime.now(), itemIds, page);
            case FUTURE:
                return bookingRepository.findByStartAfterAndItemIdIn(LocalDateTime.now(), itemIds, page);
            case PAST:
                return bookingRepository.findByStartBeforeAndEndBeforeAndItemIdIn(
                        LocalDateTime.now(), LocalDateTime.now(), itemIds, page);
            case WAITING:
                return bookingRepository.findByStatusAndItemIdIn(BookingStatus.WAITING, itemIds, page);
            case REJECTED:
                return bookingRepository.findByStatusAndItemIdIn(BookingStatus.REJECTED, itemIds, page);
            default:
                return bookingRepository.findByItemIdIn(itemIds, page);
        }
    }
}
