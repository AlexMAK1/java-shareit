package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    BookingService bookingService;

    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;

    BookingGenerator bookingGenerator;

    GetAllUserBookings getAllUserBookings;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);

        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
    }

    User user1 = new User(1L, "User1", "user1@mail");
    User user2 = new User(2L, "User2", "user2@mail");

    Item item1 = new Item("item1", "table", true, user1, 1L);
    Item item2 = new Item("item2", "chair", false, user2, 2L);
    LocalDateTime start = LocalDateTime.of(2022, 10, 23, 8, 00);
    LocalDateTime end = LocalDateTime.of(2022, 10, 23, 9, 00);

    Booking booking = new Booking(start, end, item1, user1);

    Booking booking2 = new Booking(start, end, item1, user2);
    BookingDto bookingDto = new BookingDto(1L, start, end, 1L);

    @Test
    void create() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        Booking booking1 = BookingConverter.toBooking(bookingDto, item1, user2);
        booking1.setStatus(Status.WAITING);
        when(bookingRepository.save(any()))
                .thenReturn(booking1);

        BookingDto bookingDto1 = bookingService.create(bookingDto, 2L);

        verify(bookingRepository, times(1)).save(booking1);
        assertNotNull(bookingDto1);
    }

    @Test
    void createByOwner() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.create(bookingDto, 1L));

        Assertions.assertEquals("Владелец не может создавать бронь для своей же вещи", exception.getMessage());
    }

    @Test
    void createByUnavailable() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item2));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.create(bookingDto, 1L));

        Assertions.assertEquals("Вещь в данный момент не доступна", exception.getMessage());
    }

    @Test
    void update() {
        booking.setStatus(Status.WAITING);
        when(bookingRepository.getReferenceById(anyLong()))
                .thenReturn(booking);
        item1.setId(1L);
        when(itemRepository.getReferenceById(anyLong()))
                .thenReturn(item1);
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingResponseDto bookingResponseDto1 = bookingService.update(1L, 1L, true);

        verify(bookingRepository, times(1)).save(booking);
        assertNotNull(bookingResponseDto1);
    }

    @Test
    void updateFalse() {
        booking.setStatus(Status.WAITING);
        when(bookingRepository.getReferenceById(anyLong()))
                .thenReturn(booking);
        item1.setId(1L);
        when(itemRepository.getReferenceById(anyLong()))
                .thenReturn(item1);
        when(bookingRepository.save(any()))
                .thenReturn(booking);

        BookingResponseDto bookingResponseDto1 = bookingService.update(1L, 1L, false);

        verify(bookingRepository, times(1)).save(booking);
        assertNotNull(bookingResponseDto1);
    }

    @Test
    void updateApproved() {
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.getReferenceById(anyLong()))
                .thenReturn(booking);
        item1.setId(1L);
        when(itemRepository.getReferenceById(anyLong()))
                .thenReturn(item1);

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.update(1L, 1L, true));

        Assertions.assertEquals("Данное бронирование уже подтверждено", exception.getMessage());
    }

    @Test
    void updateWrongUser() {
        booking.setStatus(Status.WAITING);
        when(bookingRepository.getReferenceById(anyLong()))
                .thenReturn(booking);
        item1.setId(1L);
        when(itemRepository.getReferenceById(anyLong()))
                .thenReturn(item1);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.update(1L, 2L, true));

        Assertions.assertEquals("Ошибка, валидация не пройдена. Пользователю с данным id вещь не принаджит",
                exception.getMessage());
    }

    @Test
    void getBookingOwner() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        item1.setId(1L);
        when(itemRepository.getReferenceById(anyLong()))
                .thenReturn(item1);

        BookingResponseDto bookingResponseDto1 = bookingService.getBooking(1L, 1L);

        verify(bookingRepository, times(1)).findById(1L);
        assertNotNull(bookingResponseDto1);
    }

    @Test
    void getBookingBooker() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking2));
        item1.setId(1L);
        when(itemRepository.getReferenceById(anyLong()))
                .thenReturn(item1);

        BookingResponseDto bookingResponseDto1 = bookingService.getBooking(1L, 2L);

        verify(bookingRepository, times(1)).findById(1L);
        assertNotNull(bookingResponseDto1);
    }

    @Test
    void getBooking() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking2));
        item1.setId(1L);
        when(itemRepository.getReferenceById(anyLong()))
                .thenReturn(item1);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(1L, 3L));

        Assertions.assertEquals("Ошибка, валидация не пройдена. Пользователю с данным id вещь не принадлежит",
                exception.getMessage());
    }

}