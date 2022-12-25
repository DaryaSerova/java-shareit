package ru.practicum.shareit;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class Fixture {

    public static User generateUser() {
        return new User(
                1L,
                "John",
                "john.doe@mail.com");
    }

    public static UserDto generateUserDto() {
        return new UserDto(
                1L,
                "John",
                "john.doe@mail.com");
    }

    public static BookingCreateDto generateBookingCreateDto(Long itemId) {
        return BookingCreateDto.builder()
                .id(1L)
                .bookerId(2L)
                .itemId(itemId)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .build();
    }

    public static Booking generateBooking(Long itemId) {
        return Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.WAITING)
                .build();
    }


    public static ItemDto generateItemDto(Long ownerId) {
        return ItemDto.builder()
                .id(1L)
                .ownerId(ownerId)
                .available(true)
                .description("test")
                .name("test")
                .build();
    }


    public static CommentDto generateCommentDto(Long authorId, Long itemId) {
        return CommentDto.builder()
                .id(1L)
                .authorId(authorId)
                .itemId(itemId)
                .text("test")
                .build();
    }

    public static RequestDto generateRequestDto(Long requestorId) {
        return RequestDto.builder()
                .id(1L)
                .requestorId(requestorId)
                .created(LocalDateTime.now())
                .description("test")
                .build();
    }
}
