package ru.practicum.shareit.booking.mapper;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    public Booking toModel(BookingDto dto) {

        return Booking.builder()
                .id(dto.getId())
                .booker(userMapper.toUser(dto.getBooker()))
                .start(dto.getStart())
                .end(dto.getEnd())
                .status(dto.getStatus())
                .item(itemMapper.toItem(dto.getItem(), dto.getItem().getOwnerId()))
                .build();
    }

    public Booking toModel(BookingCreateDto dto, UserDto user, ItemDto item) {

        return Booking.builder()
                .id(dto.getId())
                .booker(userMapper.toUser(user))
                .start(dto.getStart())
                .end(dto.getEnd())
                .status(dto.getStatus())
                .item(itemMapper.toItem(item, item.getOwnerId()))
                .build();
    }

    public BookingDto toDto(Booking entity) {

        return BookingDto.builder()
                .id(entity.getId())
                .booker(userMapper.toUserDto(entity.getBooker()))
                .start(entity.getStart())
                .end(entity.getEnd())
                .status(entity.getStatus())
                .item(itemMapper.toItemDto(entity.getItem()))
                .build();
    }
}
