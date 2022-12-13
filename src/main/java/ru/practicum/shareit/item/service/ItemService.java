package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Long ownerId, ItemDto itemDto);

    ItemDto updateItem(Long ownerId, ItemDto itemDto, Long id);

    ItemDto getItemById(Long ownerId, Long id);

    List<ItemOwnerDto> getAllItemsByOwnerId(Long ownerId);

    List<ItemOwnerDto> getAvailableItemsByName(String name);

    CommentDto addItemComment(Long ownerId, Long itemId, CommentDto commentDto);
}
