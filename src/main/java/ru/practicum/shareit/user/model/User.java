package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class User {

    private Long id;
    private String name;
    private String email;
    private final List<Item> items = new ArrayList<>();

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
