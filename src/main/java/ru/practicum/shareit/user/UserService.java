package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;

import java.util.Map;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Map<Long, User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUser(long id) {
        return userStorage.getUser(id);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user, long id) {
        return userStorage.update(user, id);
    }

    public void delete(long id) {
        userStorage.delete(id);
    }
}
