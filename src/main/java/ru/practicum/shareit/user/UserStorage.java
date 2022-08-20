package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Map;

public interface UserStorage {

    User create(User user);

    User getUser(long id);

    Map<Long, User> getUsers();

    User update(User user, long id);

    void delete(long id);
}
