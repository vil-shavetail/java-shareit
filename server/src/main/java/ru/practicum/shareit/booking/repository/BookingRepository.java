package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findByBookerIdAndStatusAndEndBeforeOrderByStartDesc(
            Long bookerId,
            BookingStatus status,
            LocalDateTime currentDateTime
    );

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.status = :status " +
            "AND b.start <= :currentDateTime " +
            "AND b.end >= :currentDateTime " +
            "ORDER BY b.start ASC")
    List<Booking> findCurrentBookingsByBooker(
            @Param("bookerId") Long bookerId,
            @Param("status") BookingStatus status,
            @Param("currentDateTime") LocalDateTime currentDateTime
    );

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.item.id = :itemId " +
            "AND b.status = :status " +
            "AND b.end >= :currentDateTime " +
            "ORDER BY b.start ASC")
    List<Booking> findNotFinishedBookingsByBookerAndItem(
            @Param("bookerId") Long bookerId,
            @Param("itemId") Long itemId,
            @Param("status") BookingStatus status,
            @Param("currentDateTime") LocalDateTime currentDateTime
    );

    List<Booking> findByBookerIdAndStatusAndStartAfterOrderByStartAsc(
            Long bookerId,
            BookingStatus status,
            LocalDateTime currentDateTime
    );

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    List<Booking> findBookingsByItemIdOrderByStartDesc(Long itemId);

    List<Booking> findByItemOwnerIdAndStatusAndEndBeforeOrderByStartDesc(
            Long ownerId,
            BookingStatus status,
            LocalDateTime currentDateTime
    );

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.status = :status " +
            "AND b.start <= :currentDateTime " +
            "AND b.end >= :currentDateTime " +
            "ORDER BY b.start ASC")
    List<Booking> findCurrentBookingsByItemOwner(
            @Param("ownerId") Long ownerId,
            @Param("status") BookingStatus status,
            @Param("currentDateTime") LocalDateTime currentDateTime
    );

    List<Booking> findByItemOwnerIdAndStatusAndStartAfterOrderByStartAsc(
            Long ownerId,
            BookingStatus status,
            LocalDateTime currentDateTime
    );
}