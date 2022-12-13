package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserPersistService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    private final CommentMapper commentMapper;
    private final UserPersistService userPersistService;

    public ItemDto toItemDto(Item item) {

        var itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .ownerId(item.getOwnerId())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .build();
        if (item.getComments() != null && !item.getComments().isEmpty()) {
            var comments = item.getComments().stream()
                    .map(commentMapper::toDto)
                    .collect(Collectors.toList());
            itemDto.setComments(comments);
        } else {
            itemDto.setComments(new ArrayList<>());
        }
        return itemDto;
    }

    public ItemDto toItemDto(Item item, List<Booking> bookings) {

        var itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .ownerId(item.getOwnerId())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .build();
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
        if (bookings != null && !bookings.isEmpty()) {
            itemDto.setLastBooking(toDto(bookings.get(0)));
            if (bookings.size() > 1) {
                itemDto.setNextBooking(toDto(bookings.get(1)));
            }
        }
        return itemDto;
    }

    public Item toItem(ItemDto itemDto, Long ownerId) {
        var item = Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .ownerId(ownerId)
                .available(itemDto.getAvailable())
                .requestId(itemDto.getRequestId())
                .build();

        if (itemDto.getComments() != null && !itemDto.getComments().isEmpty()) {
            var comments = itemDto.getComments().stream()
                    .map(commentMapper::toModel)
                    .collect(Collectors.toList());
            item.setComments(comments);
        } else {
            itemDto.setComments(new ArrayList<>());
        }

        return item;
    }

    public Item toItem(ItemDto itemDto, Item item) {
        if (itemDto.getName() != null && !itemDto.getName().isEmpty()) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty()) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }

    public ItemOwnerDto toItemOwnerDto(Item item, List<Booking> bookings) {

        var itemDto = ItemOwnerDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (bookings != null && !bookings.isEmpty()) {
            itemDto.setLastBooking(toDto(bookings.get(0)));
            if (bookings.size() > 1) {
                itemDto.setNextBooking(toDto(bookings.get(1)));
            }
        }
        return itemDto;
    }


    public BookingCreateDto toDto(Booking entity) {

        return BookingCreateDto.builder()
                .id(entity.getId())
                .start(entity.getStart())
                .end(entity.getEnd())
                .status(entity.getStatus())
                .bookerId(entity.getBooker().getId())
                .build();
    }
}
