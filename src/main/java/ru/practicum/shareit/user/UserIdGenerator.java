package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;

@Service
public class UserIdGenerator {
    private long id = 0L;

    public long generate() {
        return ++id;
    }
}
