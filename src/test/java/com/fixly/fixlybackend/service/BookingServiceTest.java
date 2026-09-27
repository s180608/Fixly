package com.fixly.fixlybackend.service;

import com.fixly.fixlybackend.repository.BookingRepository;
import com.fixly.fixlybackend.repository.ServiceRepository;
import com.fixly.fixlybackend.repository.UserRepository;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fixly.fixlybackend.model.Booking;
import com.fixly.fixlybackend.model.BookingStatus;
import com.fixly.fixlybackend.model.User;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBookingShouldRejectDoubleBooking() {

        User user = new User();
        user.setId(1L);

        com.fixly.fixlybackend.model.Service service =
                new com.fixly.fixlybackend.model.Service();
        service.setId(1L);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setService(service);
        booking.setBookingDate(LocalDate.now().plusDays(5));
        booking.setBookingTime(LocalTime.of(10, 0));
        booking.setAddress("10 London Road");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(serviceRepository.findById(1L))
                .thenReturn(Optional.of(service));

        when(bookingRepository
                .existsByServiceIdAndBookingDateAndBookingTimeAndStatusNot(
                        1L,
                        booking.getBookingDate(),
                        booking.getBookingTime(),
                        BookingStatus.CANCELLED
                ))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> bookingService.createBooking(booking)
                );

        assertEquals(
                "Service is already booked for this date and time",
                exception.getMessage()
        );
    }

    @Test
    void createBookingShouldSetPendingStatus() {

        User user = new User();
        user.setId(1L);

        com.fixly.fixlybackend.model.Service service =
                new com.fixly.fixlybackend.model.Service();
        service.setId(1L);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setService(service);
        booking.setBookingDate(LocalDate.now().plusDays(5));
        booking.setBookingTime(LocalTime.of(10, 0));
        booking.setAddress("10 London Road");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(serviceRepository.findById(1L))
                .thenReturn(Optional.of(service));

        when(bookingRepository
                .existsByServiceIdAndBookingDateAndBookingTimeAndStatusNot(
                        1L,
                        booking.getBookingDate(),
                        booking.getBookingTime(),
                        BookingStatus.CANCELLED
                ))
                .thenReturn(false);

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Booking savedBooking =
                bookingService.createBooking(booking);

        assertEquals(
                BookingStatus.PENDING,
                savedBooking.getStatus()
        );
    }

    @Test
    void createBookingShouldRejectPastDate() {

        Booking booking = new Booking();
        booking.setBookingDate(LocalDate.now().minusDays(1));
        booking.setBookingTime(LocalTime.of(10, 0));

        User user = new User();
        user.setId(1L);
        booking.setUser(user);

        com.fixly.fixlybackend.model.Service service =
                new com.fixly.fixlybackend.model.Service();
        service.setId(1L);
        booking.setService(service);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(serviceRepository.findById(1L))
                .thenReturn(Optional.of(service));

        when(bookingRepository
                .existsByServiceIdAndBookingDateAndBookingTimeAndStatusNot(
                        1L,
                        booking.getBookingDate(),
                        booking.getBookingTime(),
                        BookingStatus.CANCELLED
                ))
                .thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> bookingService.createBooking(booking)
                );

        assertEquals(
                "Booking date cannot be in the past",
                exception.getMessage()
        );
    }
    @Test
    void cancelBookingShouldSetStatusToCancelled() {

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.PENDING);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Booking cancelledBooking =
                bookingService.cancelBooking(1L);

        assertEquals(
                BookingStatus.CANCELLED,
                cancelledBooking.getStatus()
        );
    }

    @Test
    void confirmBookingShouldSetStatusToConfirmed() {

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.PENDING);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Booking confirmedBooking =
                bookingService.confirmBooking(1L);

        assertEquals(
                BookingStatus.CONFIRMED,
                confirmedBooking.getStatus()
        );
    }

    @Test
    void completeBookingShouldSetConfirmedBookingToCompleted() {

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Booking completedBooking =
                bookingService.completeBooking(1L);

        assertEquals(
                BookingStatus.COMPLETED,
                completedBooking.getStatus()
        );
    }

    @Test
    void completeBookingShouldRejectPendingBooking() {

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.PENDING);

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> bookingService.completeBooking(1L)
                );

        assertEquals(
                "Only confirmed bookings can be completed",
                exception.getMessage()
        );
    }
}