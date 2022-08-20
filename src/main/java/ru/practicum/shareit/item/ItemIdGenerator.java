package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;

@Service
public class ItemIdGenerator {
    private long id = 0L;

    public long generate() {
        return ++id;
    }
}
