package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final UserIdGenerator userIdGenerator;


    @Autowired
    public InMemoryUserStorage(UserIdGenerator userIdGenerator) {
        this.userIdGenerator = userIdGenerator;
    }

    @Override
    public Map<Long, User> getUsers() {
        return new HashMap<>(users);
    }

    @Override
    public User getUser(long id) {
        log.info("Находим пользователя с id: {} {}", id, users.get(id));
        return users.get(id);
    }

    @Override
    public User create(User user) {
        validation(user);
        long newId = userIdGenerator.generate();
        user.setId(newId);
        log.info("Сохраняем нового пользователя: {}", user);
        users.put(newId, user);
        return user;
    }

    @Override
    public User update(User user, long id) {
        user.setId(id);
        validation(user);
        for (User oldUser : users.values()) {
            if (oldUser.getId().equals(user.getId())) {
                Optional<String> email = Optional.ofNullable(user.getEmail());
                user.setEmail(email.orElse(oldUser.getEmail()));
                Optional<String> name = Optional.ofNullable(user.getName());
                user.setName(name.orElse(oldUser.getName()));
                users.put(user.getId(), user);
                log.info("Обновляем данные пользователя: {}", user);
            }
        }
        return user;
    }

    @Override
    public void delete(long id) {
        log.info("Удаляем пользователя c id: {}", id);
        users.remove(id);
    }

    private void validation(User user) {

        for (User novaUser : users.values()) {
            if (novaUser.getEmail().equals(user.getEmail())) {
                log.error("Ошибка, валидация не пройдена. Пользователь с данной электронной почтой уже существует: {}",
                        user.getEmail());
                throw new ConflictException("Ошибка, валидация не пройдена. Пользователь с данной электронной почтой " +
                        "уже существует");
            }
        }
    }
}

