package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingByItemId(Long bookerId);

    List<Booking> findBookingByItemIdAndStatusNotInAndStartBefore(
            Long bookerId, List<BookingStatus> statuses, LocalDateTime start);

    List<Booking> findByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(
            Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findByBookerIdAndStartBeforeAndEndBefore(
            Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);


    List<Booking> findByItemIdIn(List<Long> itemIds, Pageable pageable);

    List<Booking> findByStartBeforeAndEndAfterAndItemIdIn(
            LocalDateTime start, LocalDateTime end, List<Long> itemIds, Pageable pageable);

    List<Booking> findByStatusAndItemIdIn(BookingStatus status, List<Long> itemIds, Pageable pageable);

    List<Booking> findByStartBeforeAndEndBeforeAndItemIdIn(
            LocalDateTime start, LocalDateTime end, List<Long> itemIds, Pageable pageable);

    List<Booking> findByStartAfterAndItemIdIn(
            LocalDateTime start, List<Long> itemIds, Pageable pageable);
}
