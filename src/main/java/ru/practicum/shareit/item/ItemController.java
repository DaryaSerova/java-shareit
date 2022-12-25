package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.auth.AuthConstant.OWNER_ID;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/items")
public class ItemController {


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
    public ItemDto getItemById(@RequestHeader(OWNER_ID) Long ownerId,
                               @PathVariable("itemId") Long id) {
        log.info("запрос на получение предмета по id: " + id);
        return itemService.getItemById(ownerId, id);
    }

    @GetMapping
    public List<ItemOwnerDto> getAllItemsByOwnerId(@RequestHeader(OWNER_ID) Long ownerId,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(defaultValue = "20") @PositiveOrZero Integer size) {
        log.info("запрос на получение всех предметов пользователя с id: " + ownerId);
        return itemService.getAllItemsByOwnerId(ownerId, from, size).getContent();
    }

    @GetMapping("/search")
    public List<ItemOwnerDto> getAvailableItemsByName(@RequestHeader(OWNER_ID) Long ownerId,
                                                      @RequestParam String text,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                      @RequestParam(defaultValue = "20") @PositiveOrZero Integer size) {
        log.info("запрос на получение всех доступных предметов с именем: " + text);
        return itemService.getAvailableItemsByName(text, from, size).getContent();

    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addItemComment(@RequestHeader(OWNER_ID) Long ownerId,
                                     @PathVariable("itemId") Long itemId,
                                     @RequestBody CommentDto commentDto) {
        log.info("запрос на добавление комментария : " + commentDto);
        return itemService.addItemComment(ownerId, itemId, commentDto);
    }
}

