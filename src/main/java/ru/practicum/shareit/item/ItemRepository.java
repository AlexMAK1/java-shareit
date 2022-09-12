package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findItemsByOwner(User owner);

    List<Item> findByDescriptionContainingIgnoreCaseAndAvailableTrue(String text);

    Item findDistinctTopByOwner(User owner);

    List<Item> findByOwner(User owner);

}
