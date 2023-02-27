package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingByBooker_IdOrderByIdDesc(Long userId, Pageable pageable);

    List<Booking> findBookingByBookerIdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findBookingByBookerIdAndEndIsBeforeAndStatusIs(Long userId, LocalDateTime now, BookingStatus approved, Pageable pageable);

    List<Booking> findBookingByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findBookingByBookerIdAndStatus(Long userId, BookingStatus waiting, Pageable pageable);

    List<Booking> findBookingByItemOwnerIdOrderByIdDesc(Long userId, Pageable pageable);

    List<Booking> findBookingByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime now, LocalDateTime now1, Pageable pageable);

    List<Booking> findBookingByItemOwnerIdAndEndIsBeforeAndStatusIs(Long userId, LocalDateTime now, BookingStatus approved,Pageable pageable);

    List<Booking> findBookingByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime now,Pageable pageable);

    List<Booking> findBookingByItemOwnerIdAndStatus(Long userId, BookingStatus waiting,Pageable pageable);

    List<Booking> findBookingByItem_Id(Long userId);

    Optional<Booking> findFirstByItem_IdAndBooker_Id(Long itemId, Long userId);

    List<Booking> findAllByItemOwnerId(Long userId);
}