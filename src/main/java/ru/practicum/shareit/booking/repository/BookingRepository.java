package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
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

    Page<Booking> findByBookerId(Long bookerId, Pageable pageable);

    Page<Booking> findByBookerIdAndStartBeforeAndEndAfter(
            Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    Page<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findByBookerIdAndStartBeforeAndEndBefore(
            Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);


    Page<Booking> findByItemIdIn(List<Long> itemIds, Pageable pageable);

    Page<Booking> findByStartBeforeAndEndAfterAndItemIdIn(
            LocalDateTime start, LocalDateTime end, List<Long> itemIds, Pageable pageable);

    Page<Booking> findByStatusAndItemIdIn(BookingStatus status, List<Long> itemIds, Pageable pageable);

    Page<Booking> findByStartBeforeAndEndBeforeAndItemIdIn(
            LocalDateTime start, LocalDateTime end, List<Long> itemIds, Pageable pageable);

    Page<Booking> findByStartAfterAndItemIdIn(
            LocalDateTime start, List<Long> itemIds, Pageable pageable);
}
