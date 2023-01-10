package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/items")
public class ItemController {

    public static final String OWNER_ID = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(OWNER_ID) Long ownerId,
                                          @RequestBody ItemDto itemDto) {
        log.info("запрос на добавление предмета: " + itemDto);
        return itemClient.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(OWNER_ID) Long ownerId,
                                             @RequestBody ItemDto itemDto,
                                             @PathVariable("itemId") Long id) {
        log.info("запрос на редактирование предмета.");
        return itemClient.updateItem(ownerId, itemDto, id);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(OWNER_ID) Long ownerId,
                                              @PathVariable("itemId") Long id) {
        log.info("запрос на получение предмета по id: " + id);
        return itemClient.getItem(ownerId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwnerId(@RequestHeader(OWNER_ID) Long ownerId,
                                                       @RequestParam(defaultValue = "0")
                                                       @PositiveOrZero Integer from,
                                                       @RequestParam(defaultValue = "20")
                                                       @PositiveOrZero Integer size) {
        log.info("запрос на получение всех предметов пользователя с id: " + ownerId);
        return itemClient.getItemsByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAvailableItemsByName(@RequestHeader(OWNER_ID) Long ownerId,
                                                          @RequestParam String text,
                                                          @RequestParam(defaultValue = "0")
                                                          @PositiveOrZero Integer from,
                                                          @RequestParam(defaultValue = "20")
                                                          @PositiveOrZero Integer size) {
        log.info("запрос на получение всех доступных предметов с именем: " + text);
        return itemClient.getItemsByName(ownerId, text, from, size);

    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addItemComment(@RequestHeader(OWNER_ID) Long ownerId,
                                                 @PathVariable("itemId") Long itemId,
                                                 @RequestBody CommentDto commentDto) {
        log.info("запрос на добавление комментария : " + commentDto);
        return itemClient.addComment(ownerId, itemId, commentDto);
    }
}

