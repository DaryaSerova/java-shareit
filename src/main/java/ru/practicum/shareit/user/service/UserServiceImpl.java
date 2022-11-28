package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserDuplicateEmailException;
import ru.practicum.shareit.exceptions.UserEmptyEmailException;
import ru.practicum.shareit.exceptions.UserInvalidEmailException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.jpa.UserPersistService;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserPersistService userPersistService;
    private final UserMapper userMapper;
    private Pattern emailPattern = Pattern.compile("^.+@.+\\..+$");

    @Override
    public UserDto createUser(UserDto user) {

        user.setId(null);

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new UserEmptyEmailException("Email не может быть пустым.");
        }

        if (!emailPattern.matcher(user.getEmail()).matches()) {
            throw new UserInvalidEmailException("Email не может быть пустым.");
        }

        Optional<User> duplicateUser = userPersistService.findUserByEmail(user.getEmail());
        if (duplicateUser.isPresent()) {
            throw new UserDuplicateEmailException(String.format("Пользователь с email %s уже существует."));
        }

        UserDto userResult = userMapper.toUserDto(userPersistService.createUser(userMapper.toUser(user)));
        return userResult;
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) {

        Optional<User> userInDb = userPersistService.findUserById(id);

        if (userInDb.isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        userDto.setId(id);
        UserDto userResult = userMapper.toUserDto(userPersistService
                .updateUser(userMapper.toUser(userDto, userInDb.get())));
        return userResult;
    }

    @Override
    public UserDto getUser(Long id) {
        Optional<User> user = userPersistService.findUserById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        UserDto userResult = userMapper.toUserDto(user.get());
        return userResult;
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userPersistService.findAllUsers();
        return users.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        userPersistService.deleteUser(id);
    }
}
