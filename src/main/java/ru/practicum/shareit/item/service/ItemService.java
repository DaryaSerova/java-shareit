package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemToRequestDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Long ownerId, ItemDto itemDto);

    ItemDto updateItem(Long ownerId, ItemDto itemDto, Long id);

    ItemDto getItemById(Long ownerId, Long id);

    List<ItemToRequestDto> getItemsByRequestId(Long requestId);

    Page<ItemOwnerDto> getAllItemsByOwnerId(Long ownerId, Integer from,
                                            Integer size);

    Page<ItemOwnerDto> getAvailableItemsByName(String name, Integer from,
                                               Integer size);

    CommentDto addItemComment(Long ownerId, Long itemId, CommentDto commentDto);
}
