package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemToRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserPersistService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true), uses = {CommentMapper.class, UserPersistService.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapper {


    ItemDto toItemDto(Item item);

    @AfterMapping
    default void afterMappingItemDto(Item item,
                                     @MappingTarget ItemDto itemDto,
                                     @Context CommentMapper commentMapper) {

        if (item.getComments() != null && !item.getComments().isEmpty()) {
            var comments = item.getComments().stream()
                    .map(commentMapper::toDto)
                    .collect(Collectors.toList());
            itemDto.setComments(comments);
        } else {
            itemDto.setComments(new ArrayList<>());
        }
    }

    ItemDto toItemDto(Item item, List<Booking> bookings,
                      @Context CommentMapper commentMapper,
                      @Context UserPersistService userPersistService);

    @AfterMapping
    default void afterMappingItemDto(Item item,
                                     @MappingTarget ItemDto itemDto,
                                     @Context CommentMapper commentMapper,
                                     @Context UserPersistService userPersistService) {
        if (item.getComments() != null && !item.getComments().isEmpty()) {
            var comments = item.getComments().stream()
                    .map(commentMapper::toDto)
                    .peek(el -> el.setAuthorName(userPersistService
                            .findUserById(el.getAuthorId()).get()
                            .getName()))
                    .collect(Collectors.toList());
            itemDto.setComments(comments);
        } else {
            itemDto.setComments(new ArrayList<>());
        }
    }

    @AfterMapping
    default void afterMappingItemOwnerDto(List<Booking> bookings,
                                          @MappingTarget ItemDto itemDto) {
        if (bookings != null && !bookings.isEmpty()) {
            itemDto.setLastBooking(toDto(bookings.get(0)));
            if (bookings.size() > 1) {
                itemDto.setNextBooking(toDto(bookings.get(1)));
            }
        }
    }

    @Mapping(target = "ownerId", source = "ownerId")
    Item toItem(ItemDto itemDto, Long ownerId);

    void mergeToItem(ItemDto itemDto, @MappingTarget Item item);

    @AfterMapping
    default void afterMergeToItem(ItemDto itemDto, @MappingTarget Item item) {

        if (itemDto.getName() != null && !itemDto.getName().isEmpty()) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty()) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
    }

    ItemOwnerDto toItemOwnerDto(Item item, List<Booking> bookings);

    @AfterMapping
    default void afterMappingItemOwnerDto(List<Booking> bookings,
                                          @MappingTarget ItemOwnerDto itemOwnerDto) {
        if (bookings != null && !bookings.isEmpty()) {
            itemOwnerDto.setLastBooking(toDto(bookings.get(0)));
            if (bookings.size() > 1) {
                itemOwnerDto.setNextBooking(toDto(bookings.get(1)));
            }
        }
    }

    @Mapping(target = "bookerId", source = "entity.booker.id")
    @Mapping(target = "itemId", source = "entity.item.id")
    BookingCreateDto toDto(Booking entity);

    @Mapping(target = "available", source = "item.available")
    ItemToRequestDto itemToItemToRequestDto(Item item);

}
