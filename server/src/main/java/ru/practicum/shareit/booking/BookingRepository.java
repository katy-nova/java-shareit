package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.dto.BookingDtoSimple;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndEndBeforeOrderByEndAsc(Long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findByBookerIdAndStartAfterOrderByStartAsc(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findByBookerIdAndEndAfterAndStartBeforeOrderByStartAsc(Long bookerId, LocalDateTime end, LocalDateTime start);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.end > :now AND b.start < :now ORDER BY b.start ASC")
    List<Booking> findCurrentBookingsByBooker(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, Pageable pageable);

    List<Booking> findByBookerIdAndStatusOrderByStartAsc(Long bookerId, Status status, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartAsc(Long bookerId, Status status, Pageable pageable);

    List<Booking> findByItemOwnerIdOrderByStartAsc(Long itemOwnerId, Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByEndAsc(Long itemOwnerId, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartAsc(Long itemOwnerId, LocalDateTime start, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.end > :now AND b.start < :now")
    List<Booking> findCurrentBookingsByItemOwnerId(@Param("ownerId") Long bookerId, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingDtoSimple(b.id, b.start, b.end) FROM Booking b WHERE b.item.id = :itemId AND b.start > CURRENT_TIMESTAMP ORDER BY b.start ASC")
    BookingDtoSimple findFirstByItemIdAndStartAfterNow(Long itemId);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingDtoSimple(b.id, b.start, b.end) FROM Booking b WHERE b.item.id = :itemId AND b.end < CURRENT_TIMESTAMP ORDER BY b.end DESC")
    BookingDtoSimple findFirstByItemIdAndEndBeforeNow(Long itemId);

    Optional<Booking> findFirstByBookerIdAndItemIdOrderByStartAsc(Long bookerId, Long itemId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId AND b.end = " +
            "(SELECT MAX(b1.end) FROM Booking b1 " +
            "WHERE b1.item.id = b.item.id AND b1.end <= :now)")
    List<Booking> findAllLastBookings(@Param("now") LocalDateTime now, @Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId AND (b.start = " +
            "(SELECT MIN(b1.start) FROM Booking b1 WHERE b1.item.id = b.item.id AND b1.start >= :now))")
    List<Booking> findAllNextBookings(@Param("now") LocalDateTime now, @Param("ownerId") Long ownerId);
}
