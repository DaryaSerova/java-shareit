package ru.practicum.shareit.request.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestPersistService {

    Request addRequest(Request request);

    Optional<Request> findRequestById(Long requestId);

    List<Request> findAllByRequestorId(Long requestorId);

    Page<Request> findAllByRequestorIdNot(Long requestorId, Pageable pageable);

}
