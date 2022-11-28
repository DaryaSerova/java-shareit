package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface  UserService {

    UserDto createUser(UserDto user);

    UserDto updateUser(UserDto user, Long id);

    UserDto getUser(Long userId);

    List<UserDto> findAllUsers();

    void deleteUser(Long userId);

}
