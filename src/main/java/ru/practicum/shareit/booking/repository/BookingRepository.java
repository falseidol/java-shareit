package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {
    List<Booking> findBookingByBooker_IdOrderByIdDesc(Long userId);

    List<Booking> findBookingByBookerIdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findBookingByBookerIdAndEndIsBeforeAndStatusIs(Long userId, LocalDateTime now, BookingStatus approved);

    List<Booking> findBookingByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findBookingByBookerIdAndStatus(Long userId, BookingStatus waiting);

    List<Booking> findBookingByItemOwnerIdOrderByIdDesc(Long userId);

    List<Booking> findBookingByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findBookingByItemOwnerIdAndEndIsBeforeAndStatusIs(Long userId, LocalDateTime now, BookingStatus approved);

    List<Booking> findBookingByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findBookingByItemOwnerIdAndStatus(Long userId, BookingStatus waiting);

    List<Booking> findBookingByItem_Id(Long userId);

    Optional<Booking> findFirstByItem_IdAndBooker_Id(Long itemId, Long userId);

    List<Booking> findAllByItemOwnerId(Long userId);
}