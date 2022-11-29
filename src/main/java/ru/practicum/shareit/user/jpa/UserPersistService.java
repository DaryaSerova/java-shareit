package ru.practicum.shareit.user.jpa;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;


public interface UserPersistService {

    User createUser(User user);

    User updateUser(User user);

    Optional<User> findUserById(Long id);

    Optional<User> findUserByEmail(String email);

    List<User> findAllUsers();

    void deleteUser(Long id);

}
