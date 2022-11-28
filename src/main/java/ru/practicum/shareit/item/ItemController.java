package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/items")
public class ItemController {

    private static final String OWNER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader(OWNER_ID) Long ownerId,
                           @RequestBody ItemDto itemDto) {
        log.info("запрос на добавление предмета: " + itemDto);
        return itemService.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(OWNER_ID) Long ownerId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable("itemId") Long id) {
        log.info("запрос на редактирование предмета.");
        return itemService.updateItem(ownerId, itemDto, id);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") Long id) {
        log.info("запрос на получение предмета по id: " + id);
        return itemService.getItemById(id);
    }

    @GetMapping
    public List<ItemOwnerDto> getAllItemsByOwnerId(@RequestHeader(OWNER_ID) Long ownerId) {
        log.info("запрос на получение всех предметов пользователя с id: " + ownerId);
        return itemService.getAllItemsByOwnerId(ownerId);
    }


    @GetMapping("/search")
    public List<ItemOwnerDto> getAvailableItemsByName(@RequestHeader(OWNER_ID) Long ownerId,
                                                 @RequestParam String text) {
        log.info("запрос на получение всех доступных предметов с именем: " + text);
        return itemService.getAvailableItemsByName(text);

    }
}

