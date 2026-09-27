package com.fixly.fixlybackend.controller;
import java.time.LocalDate;
import com.fixly.fixlybackend.model.Booking;
import com.fixly.fixlybackend.service.BookingService;
import org.springframework.web.bind.annotation.*;
import com.fixly.fixlybackend.model.BookingStatus;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.fixly.fixlybackend.dto.BookingResponse;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<BookingResponse> getAllBookings() {

        return bookingService.getAllBookings()
                .stream()
                .map(bookingService::toBookingResponse)
                .toList();
    }

    @PostMapping
    public BookingResponse createBooking(
            @RequestBody Booking booking,
            Authentication authentication) {

        String email = authentication.getName();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (booking.getUser() == null ||
                booking.getUser().getId() == null) {

            throw new IllegalArgumentException(
                    "User is required"
            );
        }

        if (!isAdmin &&
                !bookingService.isUserAccountOwner(
                        booking.getUser().getId(),
                        email)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only create bookings for your own account"
            );
        }

        Booking createdBooking =
                bookingService.createBooking(booking);

        return bookingService.toBookingResponse(createdBooking);
    }

    @GetMapping("/{id}")
    public BookingResponse getBookingById(
            @PathVariable Long id,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin &&
                !bookingService.isBookingOwner(
                        id,
                        authentication.getName())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only view your own booking"
            );
        }

        Booking booking = bookingService.getBookingById(id);

        return bookingService.toBookingResponse(booking);
    }

    @DeleteMapping("/{id}")
    public void deleteBooking(
            @PathVariable Long id,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin &&
                !bookingService.isBookingOwner(
                        id,
                        authentication.getName())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only delete your own booking"
            );
        }

        bookingService.deleteBooking(id);
    }

    @PutMapping("/{id}")
    public BookingResponse updateBooking(
            @PathVariable Long id,
            @RequestBody Booking booking,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin &&
                !bookingService.isBookingOwner(
                        id,
                        authentication.getName())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only update your own booking"
            );
        }

        Booking updatedBooking =
                bookingService.updateBooking(id, booking);

        return bookingService.toBookingResponse(updatedBooking);
    }

    @GetMapping("/user/{userId}")
    public List<BookingResponse> getBookingsByUserId(
            @PathVariable Long userId,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin &&
                !bookingService.isUserAccountOwner(
                        userId,
                        authentication.getName())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only view your own bookings"
            );
        }

        return bookingService.getBookingsByUserId(userId)
                .stream()
                .map(bookingService::toBookingResponse)
                .toList();
    }
    @GetMapping("/service/{serviceId}")
    public List<BookingResponse> getBookingsByServiceId(
            @PathVariable Long serviceId) {

        return bookingService.getBookingsByServiceId(serviceId)
                .stream()
                .map(bookingService::toBookingResponse)
                .toList();
    }
    @PutMapping("/{id}/cancel")
    public BookingResponse cancelBooking(
            @PathVariable Long id,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin &&
                !bookingService.isBookingOwner(
                        id,
                        authentication.getName())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only cancel your own booking"
            );
        }

        Booking cancelledBooking =
                bookingService.cancelBooking(id);

        return bookingService.toBookingResponse(cancelledBooking);
    }

    @PutMapping("/{id}/confirm")
    public BookingResponse confirmBooking(@PathVariable Long id) {

        Booking confirmedBooking =
                bookingService.confirmBooking(id);

        return bookingService.toBookingResponse(confirmedBooking);
    }

    @PutMapping("/{id}/complete")
    public BookingResponse completeBooking(@PathVariable Long id) {

        Booking completedBooking =
                bookingService.completeBooking(id);

        return bookingService.toBookingResponse(completedBooking);
    }

    @GetMapping("/status/{status}")
    public List<BookingResponse> getBookingsByStatus(
            @PathVariable BookingStatus status) {

        return bookingService.getBookingsByStatus(status)
                .stream()
                .map(bookingService::toBookingResponse)
                .toList();
    }
    @GetMapping("/date/{date}")
    public List<BookingResponse> getBookingsByDate(
            @PathVariable LocalDate date) {

        return bookingService.getBookingsByDate(date)
                .stream()
                .map(bookingService::toBookingResponse)
                .toList();
    }

    @GetMapping("/date-range")
    public List<BookingResponse> getBookingsBetweenDates(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return bookingService.getBookingsBetweenDates(
                        startDate,
                        endDate
                )
                .stream()
                .map(bookingService::toBookingResponse)
                .toList();
    }
}