package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping()
    public UserDto createUser(@RequestBody UserDto user) {
        log.info("запрос на создание пользователя:" + user);
        return userService.createUser(user);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
    public UserDto updateUser(@RequestBody UserDto user,
                              @PathVariable("userId") Long id) {
        log.info("запрос на обновление пользователя:" + user + "с id " + id);
        return userService.updateUser(user, id);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        log.info("запрос на получение пользователя:" + id);
        return userService.getUser(id);
    }

    @GetMapping()
    public List<UserDto> findAllUsers() {
        log.info("запрос на получение всех пользователей");
        return userService.findAllUsers();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("запрос на удаление пользователя:" + id);
        userService.deleteUser(id);
    }
}
