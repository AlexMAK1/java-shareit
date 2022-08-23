package ru.practicum.shareit.item;


import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item create(Item item);

    Item getItem(long id);

    List<Item> getItems(long userId);

    Item update(Item item);

    List<Item> getSearchItem(String text);
}
