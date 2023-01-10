package ru.practicum.shareit.request.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestRequestorDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.service.UserPersistService;


@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true), uses = {CommentMapper.class, UserPersistService.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RequestMapper {

    @Mapping(target = "requestorId", source = "requestorId")
    Request toRequest(RequestDto requestDto, Long requestorId);

    RequestDto toRequestDto(Request request);

    RequestRequestorDto toRequestRequestorDto(Request request);

}
