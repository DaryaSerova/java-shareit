package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping()
    public ResponseEntity<Object> createUser(@RequestBody UserDto user) {
        log.info("запрос на создание пользователя:" + user);
        return userClient.add(user);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
    public ResponseEntity<Object> updateUser(@RequestBody UserDto user,
                                             @PathVariable("userId") Long id) {
        log.info("запрос на обновление пользователя:" + user + "с id " + id);
        return userClient.updateUser(id, user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        log.info("запрос на получение пользователя:" + id);
        return userClient.getUser(id);
    }

    @GetMapping()
    public ResponseEntity<Object> findAllUsers() {
        log.info("запрос на получение всех пользователей");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("запрос на удаление пользователя:" + id);
        userClient.deleteUser(id);
    }
}
