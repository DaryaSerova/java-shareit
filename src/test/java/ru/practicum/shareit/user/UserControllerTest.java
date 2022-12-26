package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserDuplicateEmailException;
import ru.practicum.shareit.user.exceptions.UserEmptyEmailException;
import ru.practicum.shareit.user.exceptions.UserInvalidEmailException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Fixture.generateUser;
import static ru.practicum.shareit.Fixture.generateUserDto;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    public void clearContext() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void shouldCreateUser() throws Exception {

        var user = generateUser();

        String result = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();


        UserDto savedUser = mapper.readValue(result, UserDto.class);
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals(user.getName(), savedUser.getName());

    }

    @Test
    public void shouldThrowUserInvalidEmailExceptionWhenCreateUserByInvalidEmail() throws Exception {

        var user = generateUser();
        user.setEmail("wrong");
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UserInvalidEmailException));

    }

    @Test
    public void shouldThrowUserInvalidEmailExceptionWhenCreateUserByEmptyEmail() throws Exception {

        var user = generateUser();
        user.setEmail(null);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UserEmptyEmailException));

    }

    @Test
    public void shouldThrowUserDuplicateEmailExceptionWhenUpdateUser() throws Exception {
        var user = generateUserDto();
        user = userService.createUser(user);
        user.setId(33L);
        user.setEmail("test2@test.ru");
        user = userService.createUser(user);
        user.setEmail("john.doe@mail.com");
        mvc.perform(patch("/users/" + user.getId())
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UserDuplicateEmailException));

    }

    @Test
    public void shouldUpdateUser() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        user.setName("test change");
        user.setEmail("test_change@test.ru");
        String result = mvc.perform(patch("/users/" + user.getId())
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        UserDto savedUser = mapper.readValue(result, UserDto.class);
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getEmail(), savedUser.getEmail());

    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhenUpdateUser() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        mvc.perform(patch("/users/" + 99L)
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UserNotFoundException));
    }


    @Test
    public void shouldGetUser() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        String result = mvc.perform(get("/users/" + user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        UserDto savedUser = mapper.readValue(result, UserDto.class);
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getEmail(), savedUser.getEmail());

    }


    @Test
    public void shouldFindAllUser() throws Exception {

        var user1 = generateUserDto();
        userService.createUser(user1);

        var user2 = generateUserDto();
        user2.setEmail("test_change@test.ru");
        userService.createUser(user2);

        var user3 = generateUserDto();
        user3.setEmail("test_change3@test.ru");
        userService.createUser(user3);

        String result = mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        var users = mapper.readValue(result, new TypeReference<List<UserDto>>() {
        });
        assertNotNull(users);
        assertEquals(3, users.size());
    }


    @Test
    public void shouldDeleteUser() throws Exception {

        var user = generateUserDto();
        user = userService.createUser(user);

        var userId = user.getId();

        mvc.perform(delete("/users/" + user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.getUser(userId)
        );
    }
}