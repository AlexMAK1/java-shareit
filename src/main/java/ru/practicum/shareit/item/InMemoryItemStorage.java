package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.InMemoryUserStorage;

import java.util.*;

@Slf4j
@Component
public class InMemoryItemStorage implements ItemStorage {

    private final InMemoryUserStorage inMemoryUserStorage;

    private final Map<Long, Item> items = new HashMap<>();

    private final ItemIdGenerator itemIdGenerator;

    @Autowired
    public InMemoryItemStorage(InMemoryUserStorage inMemoryUserStorage, ItemIdGenerator itemIdGenerator) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.itemIdGenerator = itemIdGenerator;
    }


    @Override
    public Item create(Item item) {
        if (inMemoryUserStorage.getUsers().containsKey(item.getUserId())) {
            long newId = itemIdGenerator.generate();
            item.setId(newId);
            log.info("Сохраняем новую вещь: {}", item);
            items.put(newId, item);
            inMemoryUserStorage.getUser(item.getUserId()).getItems().add(item);
        } else {
            log.error("Ошибка, пользователя с таким id не существует: {}", item.getUserId());
            throw new NotFoundException("Ошибка, пользователя с таким id не существует.");
        }
        return item;
    }

    @Override
    public Item getItem(long id) {
        log.info("Находим вещь с id: {} {}", id, items.get(id));
        return items.get(id);
    }

    @Override
    public List<Item> getItems(long userId) {
        return inMemoryUserStorage.getUsers().get(userId).getItems();
    }

    @Override
    public Item update(Item item) {
        validate(item);
        for (Item oldItem : items.values()) {
            if (oldItem.getId().equals(item.getId())) {
                Optional<String> description = Optional.ofNullable(item.getDescription());
                item.setDescription(description.orElse(oldItem.getDescription()));
                Optional<String> name = Optional.ofNullable(item.getName());
                item.setName(name.orElse(oldItem.getName()));
                Optional<Boolean> available = Optional.ofNullable(item.getAvailable());
                item.setAvailable(available.orElse(oldItem.getAvailable()));
                items.put(item.getId(), item);
                inMemoryUserStorage.getUser(item.getUserId()).getItems().clear();
                inMemoryUserStorage.getUser(item.getUserId()).getItems().add(item);
                log.info("Обновляем вещь: {}", item);
            }
        }
        return item;
    }

    @Override
    public List<Item> getSearchItem(String text) {
        String correctText = text.toLowerCase();
        List<Item> itemList = new ArrayList<>();
        if (text.isBlank()) {
            return itemList;
        }
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(correctText)
                    || item.getDescription().toLowerCase().contains(correctText))
                    && item.getAvailable()) {
                itemList.add(item);
            }
        }
        log.info("Находим все веши пользователя по запросу: {} {}", text, itemList);
        return itemList;
    }

    private void validate(Item item) {
        if (!items.get(item.getId()).getUserId().equals(item.getUserId())) {
            log.error("Ошибка, пользователя с таким id не существует: {}", item.getUserId());
            throw new NotFoundException("Ошибка, пользователя с таким id не существует.");
        }
    }
}
