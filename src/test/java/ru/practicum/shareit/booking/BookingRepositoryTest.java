package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.RequestRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    BookingRepository bookingRepository;

    User user1;
    User user2;

    Item item1;
    Item item2;

    Booking booking;

    Booking bookingAfterNow;

    Booking bookingBeforeNow;

    ItemRequest request1;
    ItemRequest request2;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1L, "User1", "user1@mail"));
        user2 = userRepository.save(new User(2L, "User2", "user2@mail"));

        item1 = itemRepository.save(new Item("item1", "table", true, user1, 1L));
        item2 = itemRepository.save(new Item("item2", "chair", true, user2, 2L));

        booking = bookingRepository.save(new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), item1, user1, Status.APPROVED));

        bookingAfterNow = bookingRepository.save(new Booking(LocalDateTime.now(), LocalDateTime.of(2022, 9, 27, 10, 15), item1, user1));

        bookingBeforeNow = bookingRepository.save(new Booking(LocalDateTime.now(), LocalDateTime.of(2022, 9, 25, 10, 15), item2, user1));

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
    void findAllByItem() {
        List<Booking> bookings = bookingRepository.findAllByItem(item1, PageRequest.ofSize(10));
        assertNotNull(bookings);
    }

    @Test
    void findAllByBooker() {
        List<Booking> bookings = bookingRepository.findAllByBooker(user1, PageRequest.ofSize(10));
        assertNotNull(bookings);
    }

    @Test
    void findAllByBookerAndStatusEquals() {
        List<Booking> bookings = bookingRepository.findAllByBookerAndStatusEquals(user1, Status.APPROVED);
        assertNotNull(bookings);
    }

    @Test
    void findAllByBookerAndStartBeforeAndEndAfter() {
        List<Booking> bookings = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user1, LocalDateTime.now(), LocalDateTime.now());
        assertNotNull(bookings);
    }

    @Test
    void findAllByBookerAndEndBefore() {
        List<Booking> bookings = bookingRepository.findAllByBookerAndEndBefore(user1, LocalDateTime.now());
        assertNotNull(bookings);
    }

    @Test
    void findAllByItemAndEndBefore() {
        List<Booking> bookings = bookingRepository.findAllByItemAndEndBefore(item1, LocalDateTime.now());
        assertNotNull(bookings);
    }

    @Test
    void findTopByItem() {
        Booking booking = bookingRepository.findTopByItem(item1);
        assertNotNull(booking);
    }

    @Test
    void findByItemAndAndEndBefore() {
        Booking booking = bookingRepository.findByItemAndAndEndBefore(item2, LocalDateTime.now());
        assertNotNull(booking);
    }

    @Test
    void findByItemAndStatusEquals() {
        Booking booking = bookingRepository.findByItemAndStatusEquals(item1, Status.APPROVED);
        assertNotNull(booking);
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        requestRepository.deleteAll();
    }
}