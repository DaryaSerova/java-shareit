package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.jpa.ItemPersistService;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.jpa.UserPersistService;

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
        ItemDto itemResult = itemMapper.toItemDto(itemPersistService
                .updateItem(itemMapper.toItem(itemDto, itemInDb.get())));

        return itemResult;

    }

    @Override
    public ItemDto getItemById(Long id) {

        Optional<Item> item = itemPersistService.findItemById(id);

        if (item.isEmpty()) {
            throw new ItemNotFoundException("Предмет не найден.");
        }

        ItemDto itemResult = itemMapper.toItemDto(item.get());
        return itemResult;
    }

    @Override
    public List<ItemOwnerDto> getAllItemsByOwnerId(Long ownerId) {

        List<Item> items = itemPersistService.findAllItemsByOwnerId(ownerId);

        if (items.isEmpty()) {
            throw new ItemNotFoundException("Предмет не найден.");
        }

        return items.stream()
                .map(itemMapper::toItemOwnerDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemOwnerDto> getAvailableItemsByName(String name) {

        if (name == null || name.isEmpty()) {
            return emptyList();
        }
        List<Item> items = itemPersistService.findAvailableItemsByName(name);

        if (items.isEmpty()) {
            throw new ItemNotFoundException("Предмет не найден.");
        }

        return items.stream()
                .map(itemMapper::toItemOwnerDto)
                .collect(Collectors.toList());
    }
}
