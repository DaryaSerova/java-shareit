package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestRequestorDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(path = "/requests")
public class RequestController {
    public static final String REQUESTOR_ID = "X-Sharer-User-Id";
    private final RequestService requestService;

    @PostMapping
    public RequestDto addRequest(@RequestHeader(REQUESTOR_ID) Long requestorId,
                                 @RequestBody RequestDto requestDto) {
        log.info("запрос на добавление вещи: " + requestDto);
        return requestService.addRequest(requestorId, requestDto);
    }

    @GetMapping("/{requestId}")
    public RequestRequestorDto getRequestById(@RequestHeader(REQUESTOR_ID) Long requestorId,
                                              @PathVariable Long requestId) {
        log.info("запрос на получение запросов пользователя с id: " + requestorId);
        return requestService.getRequestById(requestorId, requestId);
    }

    @GetMapping
    public List<RequestRequestorDto> getAllRequestByRequestorId(@RequestHeader(REQUESTOR_ID) Long requestorId) {
        log.info("запрос на получение всех запросов пользователя с id: " + requestorId);
        return requestService.getAllRequestByRequestorId(requestorId);
    }

    @GetMapping("/all")
    public List<RequestRequestorDto> getAllRequest(@RequestHeader(REQUESTOR_ID) Long requestorId,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(defaultValue = "20") @PositiveOrZero Integer size) {
        log.info("запрос на получение всех запросов.");
        return requestService.getAllRequest(requestorId, PageRequest.of(from, size,
                Sort.by("created").descending()));

    }
}
