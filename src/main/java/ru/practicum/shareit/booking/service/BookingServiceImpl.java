package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exceptions.BookingDateBadRequestException;
import ru.practicum.shareit.booking.exceptions.BookingNotFoundException;
import ru.practicum.shareit.booking.exceptions.BookingOwnerNotFoundException;
import ru.practicum.shareit.booking.exceptions.BookingStatusBadRequestException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.exceptions.ItemUnavailableException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingPersistService bookingPersistService;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingMapper mapper;


    @Override
    public BookingDto findBookingById(Long ownerId, Long bookingId) {
        var booking = getBooking(bookingId);
        userService.getUser(ownerId);

        if (!booking.getBooker().getId().equals(ownerId)) {
            var item = itemService.getItemById(ownerId, booking.getItem().getId());
            if (!item.getOwnerId().equals(ownerId)) {
                throw new BookingNotFoundException(
                        String.format("Бронирование для пользователя с id %s  не найдено",
                                ownerId.toString()));
            }
        }

        return mapper.toDto(booking);
    }

    @Override
    public BookingDto addBooking(Long ownerId, BookingCreateDto bookingDto) {
        var item = itemService.getItemById(ownerId, bookingDto.getItemId());
        if (item.getAvailable() == null || item.getAvailable() == false) {
            throw new ItemUnavailableException(String.format("Вещь не доступна для бронирования"));
        }
        if (bookingDto.getStart().compareTo(LocalDateTime.now()) < 0) {
            throw new BookingDateBadRequestException("Дата начала бронирования не корректна");
        }

        if (bookingDto.getEnd().compareTo(LocalDateTime.now()) < 0) {
            throw new BookingDateBadRequestException("Дата окончания бронирования не корректна");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BookingDateBadRequestException("Дата окончания бронирования не корректна тк она " +
                    "раньше начала бронирования");
        }
        var user = userService.getUser(ownerId);

        if (user.getId().equals(item.getOwnerId())) {
            throw new BookingOwnerNotFoundException("Бронирование вещи владельцем запрещено");
        }
        bookingDto.setBookerId(ownerId);
        bookingDto.setStatus(BookingStatus.WAITING);
        return mapper.toDto(bookingPersistService.createBooking(mapper.toModel(bookingDto, user, item)));
    }

    @Override
    public BookingDto approveBooking(Long ownerId, Long bookingId, boolean isApproved) {
        var booking = getBooking(bookingId);

        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new BookingStatusBadRequestException("Статус уже APPROVED");
        }
        if (!booking.getItem().getOwnerId().equals(ownerId)) {
            throw new BookingNotFoundException(
                    String.format("Бронирование было сделано не пользователем с id %s ",
                            ownerId.toString()));
        }
        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return mapper.toDto(bookingPersistService.updateBooking(booking));
    }

    @Override
    public List<BookingDto> getBookingForUserByState(Long ownerId, BookingState state) {
        userService.getUser(ownerId);
        return bookingPersistService.findBookingForUserByState(ownerId, state)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingForUserByItems(Long ownerId, BookingState state) {
        userService.getUser(ownerId);
        var items = itemService.getAllItemsByOwnerId(ownerId);
        if (items == null || items.isEmpty()) {
            return null;
        }
        var itemIds = items
                .stream()
                .map(el -> el.getId())
                .collect(Collectors.toList());

        return bookingPersistService.findBookingForUserByItems(ownerId, itemIds, state)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }


    private Booking getBooking(Long bookingId) {
        var bookingOpt = bookingPersistService.findBookingById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new BookingNotFoundException(String.format("Бронирование с id %s не найдено ",
                    bookingId.toString()));
        }
        return bookingOpt.get();
    }
}
