package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItem(Item item, Pageable pageable);

    List<Booking> findAllByBooker(User user, Pageable pageable);

    List<Booking> findAllByBookerAndStatusEquals(User user, Status status);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User user, LocalDateTime now, LocalDateTime current);

    List<Booking> findAllByBookerAndEndBefore(User user, LocalDateTime now);

    List<Booking> findAllByItemAndEndBefore(Item item, LocalDateTime now);

    Booking findTopByItem(Item item);

    Booking findByItemAndAndEndAfter(Item item, LocalDateTime now);

    Booking findByItemAndAndEndBefore(Item item, LocalDateTime now);

    Booking findByItemAndStatusEquals(Item item, Status status);

    Booking findByItemAndStartBeforeAndEndAfter(Item item, LocalDateTime now, LocalDateTime current);
}
