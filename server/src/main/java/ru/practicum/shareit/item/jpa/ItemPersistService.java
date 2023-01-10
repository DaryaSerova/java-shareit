package ru.practicum.shareit.item.jpa;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemPersistService {

    Item addItem(Item item);

    Item updateItem(Item item);

    Optional<Item> findItemById(Long id);

    Page<Item> findAllItemsByOwnerId(Long ownerId, Integer from, Integer size);

    Page<Item> findAvailableItemsByName(String name, Integer from, Integer size);

    List<Item> findItemsByRequestId(Long requestId);

}
