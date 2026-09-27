package com.fixly.fixlybackend.repository;
import java.util.List;
import com.fixly.fixlybackend.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import com.fixly.fixlybackend.model.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByServiceId(Long serviceId);
    List<Booking> findByUserIdOrderByBookingDateAscBookingTimeAsc(Long userId);
    List<Booking> findByServiceIdOrderByBookingDateAscBookingTimeAsc(
            Long serviceId);
    List<Booking> findByStatusOrderByBookingDateAscBookingTimeAsc(
            BookingStatus status);
    List<Booking> findByBookingDate(LocalDate bookingDate);
    List<Booking> findByBookingDateBetweenOrderByBookingDateAscBookingTimeAsc(
            LocalDate startDate,
            LocalDate endDate);
    boolean existsByServiceIdAndBookingDateAndBookingTimeAndStatusNotAndIdNot(
            Long serviceId,
            LocalDate bookingDate,
            LocalTime bookingTime,
            BookingStatus status,
            Long id);
    boolean existsByUserId(Long userId);
    boolean existsByServiceIdAndBookingDateAndBookingTimeAndStatusNot(
            Long serviceId,
            LocalDate bookingDate,
            LocalTime bookingTime,
            BookingStatus status);
}