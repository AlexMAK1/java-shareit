package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserConverter userConverter;

    @Autowired
    public UserController(UserService userService, UserConverter userConverter) {
        this.userService = userService;
        this.userConverter = userConverter;
    }

    @GetMapping
    public Collection<UserDto> findAll() {
        Collection<User> users = userService.getUsers().values();
        log.info("Находим всех существующих пользователей: {}", userService.getUsers().values());
        return users.stream()
                .map(userConverter::toUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    public UserDto getUser(@PathVariable("id") long id) {
        return userConverter.toUserDto(userService.getUser(id));
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        User user = userConverter.toUser(userDto);
        return userConverter.toUserDto(userService.create(user));
    }

    @PatchMapping("{id}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable("id") long id) {
        userDto.setId(id);
        User user = userConverter.toUser(userDto);
        return userConverter.toUserDto(userService.update(user, id));
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable("id") long id) {
        userService.delete(id);
    }
}
