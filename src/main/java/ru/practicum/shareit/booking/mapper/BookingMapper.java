package ru.practicum.shareit.booking.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;


@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true), uses = {UserMapper.class, ItemMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookingMapper {

    @Mapping(target = "booker", ignore = true)
    @Mapping(target = "item", ignore = true)
    Booking toModel(BookingDto dto);

    @AfterMapping
    default void afterMappingBooking(BookingDto dto,
                                     @MappingTarget Booking booking,
                                     @Context UserMapper userMapper,
                                     @Context ItemMapper itemMapper) {
        booking.setBooker(userMapper.toUser(dto.getBooker()));
        booking.setItem(itemMapper.toItem(dto.getItem(), dto.getItem().getOwnerId()));
    }


    @Mapping(target = "booker", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "id", source = "dto.id")
    Booking toModel(BookingCreateDto dto, UserDto user, ItemDto item,
                    @Context UserMapper userMapper,
                    @Context ItemMapper itemMapper);

    @AfterMapping
    default void afterMappingBooking(BookingCreateDto dto,
                                     UserDto user, ItemDto item,
                                     @MappingTarget Booking booking,
                                     @Context UserMapper userMapper,
                                     @Context ItemMapper itemMapper) {
        booking.setBooker(userMapper.toUser(user));
        booking.setItem(itemMapper.toItem(item, item.getOwnerId()));
    }

    @Mapping(target = "booker", ignore = true)
    @Mapping(target = "item", ignore = true)
    BookingDto toDto(Booking entity, @Context UserMapper userMapper,
                     @Context ItemMapper itemMapper);

    @AfterMapping
    default void afterMappingBookingDto(Booking entity,
                                        @MappingTarget BookingDto bookingDto,
                                        @Context UserMapper userMapper,
                                        @Context ItemMapper itemMapper) {
        bookingDto.setBooker(userMapper.toUserDto(entity.getBooker()));
        bookingDto.setItem(itemMapper.toItemDto(entity.getItem()));
    }
}
