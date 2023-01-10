package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemToRequestDto;
import ru.practicum.shareit.item.exceptions.ItemEmptyAvailableException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.jpa.ItemPersistService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.booking.service.BookingPersistService;
import ru.practicum.shareit.item.comment.exception.CommentBadRequestException;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.exceptions.ItemEmptyNameException;
import ru.practicum.shareit.user.exceptions.UserNotOwnerItemException;
import ru.practicum.shareit.user.service.UserPersistService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemPersistService itemPersistService;
    private final UserPersistService userPersistService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final BookingPersistService bookingPersistService;

    @Override
    public ItemDto addItem(Long ownerId, ItemDto itemDto) {

        if (userPersistService.findUserById(ownerId).isEmpty() || userPersistService.findUserById(ownerId) == null) {
            throw new UserNotFoundException("Владелец предмета не найден.");
        }

        if (itemDto.getAvailable() == null) {
            throw new ItemEmptyAvailableException("Статус доступности предмета не может быть пустым.");
        }

        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new ItemEmptyNameException("Название предмета не может быть пустым.");
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new ItemEmptyNameException("Описание предмета не может быть пустым.");
        }

        return itemMapper.toItemDto(itemPersistService.addItem(itemMapper.toItem(itemDto, ownerId)));
    }

    @Override
    public ItemDto updateItem(Long ownerId, ItemDto itemDto, Long id) {

        Optional<Item> itemInDb = itemPersistService.findItemById(id);

        if (itemInDb.isEmpty()) {
            throw new ItemNotFoundException("Предмет не найден.");
        }
        var ownerIdDb = itemInDb.get().getOwnerId();
        if (!ownerIdDb.equals(ownerId)) {
            throw new UserNotOwnerItemException("Пользователь не является владельцем предмета.");
        }

        itemDto.setId(id);
        var entity = itemInDb.get();
        itemMapper.mergeToItem(itemDto, entity);
        entity.setOwnerId(ownerId);
        ItemDto itemResult = itemMapper.toItemDto(itemPersistService
                .updateItem(entity));

        return itemResult;

    }

    @Override
    public ItemDto getItemById(Long ownerId, Long id) {

        Optional<Item> item = itemPersistService.findItemById(id);

        if (item.isEmpty()) {
            throw new ItemNotFoundException("Предмет не найден.");
        }
        var bookings = bookingPersistService.findBookingByItemId(id)
                .stream()
                .filter(el -> el.getItem().getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
        ItemDto itemResult = itemMapper.toItemDto(item.get(), bookings, commentMapper, userPersistService);
        return itemResult;
    }

    @Override
    public List<ItemToRequestDto> getItemsByRequestId(Long requestId) {

        var items = itemPersistService.findItemsByRequestId(requestId);
        if (items == null || items.isEmpty()) {
            return emptyList();
        }
        return items.stream()
                .map(itemMapper::itemToItemToRequestDto)
                .collect(Collectors.toList());
    }


    @Override
    public Page<ItemOwnerDto> getAllItemsByOwnerId(Long ownerId, Integer from, Integer size) {

        Page<Item> items = itemPersistService.findAllItemsByOwnerId(ownerId, from, size);
        return items
                .map(el -> itemMapper.toItemOwnerDto(el,
                        bookingPersistService.findBookingByItemId(el.getId())));
    }

    @Override
    public Page<ItemOwnerDto> getAvailableItemsByName(String name, Integer from, Integer size) {

        if (name == null || name.isEmpty()) {
            return Page.empty();
        }
        var items = itemPersistService.findAvailableItemsByName(name, from, size);

        return items
                .map(el -> itemMapper.toItemOwnerDto(el, bookingPersistService.findBookingByItemId(el.getId())));
    }

    @Override
    public CommentDto addItemComment(Long ownerId, Long itemId, CommentDto commentDto) {

        if (itemId == null || commentDto.getText() == null || commentDto.getText().isEmpty()) {
            throw new CommentBadRequestException("Не валидные параметры комментария ");
        }
        getItemById(ownerId, itemId);

        commentDto.setItemId(itemId);

        commentDto.setAuthorId(ownerId);

        var bookings = bookingPersistService.findBookingByItemIdAndStatusNotInAndStartBefore(itemId,
                List.of(BookingStatus.REJECTED), LocalDateTime.now());
        if (bookings == null || bookings.isEmpty()) {
            throw new CommentBadRequestException("Требуется бронирование для создания комментария ");
        }

        var result = commentMapper.toDto(commentRepository.save(commentMapper.toModel(commentDto)));

        var author = userPersistService.findUserById(ownerId).get();

        result.setAuthorName(author.getName());

        return result;

    }
}
