package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestRequestorDto;

import java.util.List;

public interface RequestService {

    RequestDto addRequest(Long requestorId, RequestDto requestDto);

    RequestRequestorDto getRequestById(Long requestorId, Long requestId);

    List<RequestRequestorDto> getAllRequestByRequestorId(Long requestorId);

    List<RequestRequestorDto> getAllRequest(Long requestorId, Pageable pageable);

}
