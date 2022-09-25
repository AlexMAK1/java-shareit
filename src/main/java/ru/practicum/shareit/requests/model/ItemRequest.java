package ru.practicum.shareit.requests.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "requests")
@Data
@NoArgsConstructor
public class ItemRequest {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "created")
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;
    @Transient
    private List<Item> items = new ArrayList<>();

    public ItemRequest(Long id, String description, LocalDateTime created, User requestor) {
        this.id = id;
        this.description = description;
        this.created = created;
        this.requestor = requestor;
    }
}
