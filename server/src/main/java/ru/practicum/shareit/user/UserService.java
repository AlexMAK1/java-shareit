package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto getUser(long id);

    List<UserDto> getUsers();

    UserDto update(UserDto userDto, long id);

    void delete(long id);
}
