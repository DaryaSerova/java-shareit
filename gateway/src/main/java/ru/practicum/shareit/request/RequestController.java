package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(path = "/requests")
public class RequestController {
    public static final String REQUESTOR_ID = "X-Sharer-User-Id";

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(REQUESTOR_ID) Long requestorId,
                                             @RequestBody RequestDto requestDto) {
        log.info("запрос на добавление вещи: " + requestDto);
        return requestClient.addRequest(requestorId, requestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(REQUESTOR_ID) Long requestorId,
                                                 @PathVariable Long requestId) {
        log.info("запрос на получение запросов пользователя с id: " + requestorId);
        return requestClient.getRequest(requestorId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestByRequestorId(@RequestHeader(REQUESTOR_ID) Long requestorId) {
        log.info("запрос на получение всех запросов пользователя с id: " + requestorId);
        return requestClient.getRequestsByOwner(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequest(@RequestHeader(REQUESTOR_ID) Long requestorId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = "20") @PositiveOrZero Integer size) {
        log.info("запрос на получение всех запросов.");
        return requestClient.getAllRequests(requestorId, from, size);

    }
}
