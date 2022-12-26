package ru.practicum.shareit.request.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestPersistServiceImpl implements RequestPersistService {

    private final RequestRepository requestRepository;

    @Override
    public Request addRequest(Request request) {
        request.setCreated(LocalDateTime.now());
        return requestRepository.save(request);
    }

    @Override
    public Optional<Request> findRequestById(Long requestId) {
        return requestRepository.findById(requestId);
    }

    @Override
    public List<Request> findAllByRequestorId(Long requestorId) {
        return requestRepository.findByRequestorId(requestorId,
               PageRequest.of(0, 100, Sort.by("created").descending()));
    }

    @Override
    public Page<Request> findAllByRequestorIdNot(Long requestorId, Pageable pageable) {
        return requestRepository.findByRequestorIdNot(requestorId, pageable);
    }
}
