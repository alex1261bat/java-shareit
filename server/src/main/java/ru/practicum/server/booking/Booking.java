package ru.practicum.server.booking;

import lombok.*;
import ru.practicum.server.item.Item;
import ru.practicum.server.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", schema = "public")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    private User booker;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
}
