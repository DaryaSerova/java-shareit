package ru.practicum.shareit.item.jpa;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemPersistService {

    Item addItem(Item item);

    Item updateItem(Item item);

    Optional<Item> findItemById(Long id);

    List<Item> findAllItemsByOwnerId(Long ownerId);

    List<Item> findAvailableItemsByName(String name);


}
