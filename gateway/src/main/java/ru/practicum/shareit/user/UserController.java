package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.getUsers();
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") long id) {
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto userDto) {
        return userClient.createUser(userDto);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> update(@RequestBody UserDto userDto, @PathVariable("id") long id) {
        return userClient.updateUser(id, userDto);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable("id") long id) {
        userClient.delete(id);
    }
}
