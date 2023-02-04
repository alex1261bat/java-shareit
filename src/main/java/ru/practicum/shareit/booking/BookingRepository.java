package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.exceptions.NotFoundException;


import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable pageRequest);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long userId, LocalDateTime startTime,
                                                                                 LocalDateTime endTime,
                                                                                 Pageable pageRequest);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime endTime,
                                                                  Pageable pageRequest);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime startTime,
                                                                   Pageable pageRequest);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, Status status, Pageable pageRequest);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 order by b.start desc")
    List<Booking> findAllItemOwnerBookings(Long userId, Pageable pageRequest);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1  and b.start< ?2 and b.end> ?3 order by b.start desc")
    List<Booking> findAllItemOwnerCurrentBookings(Long userId, LocalDateTime startTime, LocalDateTime endTime,
                                                  Pageable pageRequest);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 and b.end< ?2 order by b.start desc")
    List<Booking> findAllItemOwnerPastBookings(Long userId, LocalDateTime endTime, Pageable pageRequest);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 and b.start> ?2 order by b.start desc")
    List<Booking> findAllItemOwnerFutureBookings(Long userId, LocalDateTime startTime, Pageable pageRequest);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 and b.status = ?2 order by b.start desc")
    List<Booking> findAllItemOwnerBookingsByStatus(Long userId, Status status, Pageable pageRequest);

    @Query("select b from Booking b " +
            "where b.item.id = ?1")
    List<Booking> findAllByItemId(Long itemId);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 and b.item.id = ?2 and b.end <= ?3")
    List<Booking> findAllUserBookings(Long userId, Long itemId, LocalDateTime now);

    default Booking getBookingById(Long bookingId) {
        return findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не существует"));
    }
}
