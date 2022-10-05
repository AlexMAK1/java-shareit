package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    ItemRepository itemRepository;

    User user1;
    User user2;

    Item item1;
    Item item2;

    ItemRequest request1;
    ItemRequest request2;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1L, "User1", "user1@mail"));
        user2 = userRepository.save(new User(2L, "User2", "user2@mail"));

        item1 = itemRepository.save(new Item("item1", "table", true, user1, 1L));
        item2 = itemRepository.save(new Item("item2", "chair", true, user2, 2L));

        request1 = (new ItemRequest());
        request1.setDescription("request1");
        request1.setRequestor(user1);
        request1.setCreated(LocalDateTime.now());

        request2 = (new ItemRequest());
        request2.setDescription("request2");
        request2.setRequestor(user2);
        request2.setCreated(LocalDateTime.now());

        requestRepository.save(request1);
        log.info("Сохраняем новый запрос {}:", request1);
        requestRepository.save(request2);
        log.info("Сохраняем новый запрос {}:", request2);
    }


    @Test
    void findAllByRequestor() {
        final List<ItemRequest> itemRequestList = requestRepository.findByRequestor(user2);
        assertNotNull(itemRequestList);
        assertEquals(request2.getId(), itemRequestList.get(0).getId());
        assertEquals(1, itemRequestList.size());
        assertEquals("request2", itemRequestList.get(0).getDescription());
        assertEquals(user2, itemRequestList.get(0).getRequestor());
    }


    @Test
    void findByRequestor() {
        final List<ItemRequest> itemRequestList = requestRepository.findByRequestor(user1);
        assertNotNull(itemRequestList);
        assertEquals(request1.getId(), itemRequestList.get(0).getId());
        assertEquals(1, itemRequestList.size());
        assertEquals("request1", itemRequestList.get(0).getDescription());
        assertEquals(user1, itemRequestList.get(0).getRequestor());
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        requestRepository.deleteAll();
    }
}