package ru.practicum.shareit.item;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Slf4j
@Service
public class ItemService {

    private final ItemStorage itemStorage;

    @Autowired
    public ItemService(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    public Item create(Item item) {
        return itemStorage.create(item);
    }

    public Item update(Item item) {
        return itemStorage.update(item);
    }

    public Item getItem(long id) {
        return itemStorage.getItem(id);
    }

    public List<Item> getItems(long userId) {
        return itemStorage.getItems(userId);
    }

    public List<Item> getSearchItem(String text) {
        return itemStorage.getSearchItem(text);
    }
}
