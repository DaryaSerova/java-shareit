package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestRequestorDto;
import ru.practicum.shareit.request.exceptions.RequestEmptyNameException;
import ru.practicum.shareit.request.exceptions.RequestNotFoundException;
import ru.practicum.shareit.request.jpa.RequestPersistService;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final UserService userService;

    private final RequestPersistService requestPersistService;

    private final RequestMapper requestMapper;

    private final ItemService itemService;


    @Override
    public RequestDto addRequest(Long requestorId, RequestDto requestDto) {
        userService.getUser(requestorId);

        if (requestDto.getDescription() == null || requestDto.getDescription().isEmpty()) {
            throw new RequestEmptyNameException("Описание запроса не может быть пустым.");
        }

        return requestMapper.toRequestDto(
                requestPersistService.addRequest(requestMapper.toRequest(requestDto, requestorId)));
    }

    @Override
    public RequestRequestorDto getRequestById(Long requestorId, Long requestId) {
        userService.getUser(requestorId);

        var requestOpt = requestPersistService.findRequestById(requestId);

        if (requestOpt.isEmpty()) {
            throw new RequestNotFoundException("Запрос не найден");
        }

        var request = requestMapper.toRequestRequestorDto(requestOpt.get());

        request.setItems(itemService.getItemsByRequestId(request.getId()));
        return request;
    }

    @Override
    public List<RequestRequestorDto> getAllRequestByRequestorId(Long requestorId) {

        userService.getUser(requestorId);

        var requests = requestPersistService.findAllByRequestorId(requestorId);
        if (requests == null || requests.isEmpty()) {
            return Collections.emptyList();
        }

        return requests.stream()
                .map(requestMapper::toRequestRequestorDto)
                .peek(el -> {
                    el.setItems(itemService.getItemsByRequestId(el.getId()));})
                .collect(Collectors.toList());

    }

    @Override
    public List<RequestRequestorDto> getAllRequest(Long requestorId, Pageable pageable) {

        var requests = requestPersistService.findAllByRequestorIdNot(requestorId, pageable);

        return requests.stream()
                .map(requestMapper::toRequestRequestorDto)
                .peek(el -> {
                    el.setItems(itemService.getItemsByRequestId(el.getId()));
                })
                .collect(Collectors.toList());


    }
}
