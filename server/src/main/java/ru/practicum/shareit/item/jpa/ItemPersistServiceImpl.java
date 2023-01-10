package ru.practicum.shareit.item.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemPersistServiceImpl implements ItemPersistService {

    private final ItemRepository itemRepository;

    @Override
    public Item addItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Optional<Item> findItemById(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    public Page<Item> findAllItemsByOwnerId(Long ownerId, Integer from, Integer size) {
        return itemRepository.findByOwnerId(ownerId, PageRequest.of(from, size, Sort.by("id")));
    }

    @Override
    public Page<Item> findAvailableItemsByName(String name, Integer from, Integer size) {
        return itemRepository.findByNameOrDescription(name, PageRequest.of(from, size, Sort.by("id")));
    }

    @Override
    public List<Item> findItemsByRequestId(Long requestId) {
        return itemRepository.findByRequestId(requestId);
    }

}
