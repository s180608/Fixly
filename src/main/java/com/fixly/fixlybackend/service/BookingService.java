package com.fixly.fixlybackend.service;
import java.time.LocalDate;
import com.fixly.fixlybackend.model.Booking;
import com.fixly.fixlybackend.repository.BookingRepository;
import com.fixly.fixlybackend.model.BookingStatus;
import java.util.List;
import com.fixly.fixlybackend.repository.UserRepository;
import com.fixly.fixlybackend.repository.ServiceRepository;
import com.fixly.fixlybackend.model.BookingStatus;
import com.fixly.fixlybackend.model.User;
import java.util.List;
import com.fixly.fixlybackend.dto.BookingResponse;

@org.springframework.stereotype.Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    public List<Booking> getBookingsByStatus(BookingStatus status) {
        return bookingRepository
                .findByStatusOrderByBookingDateAscBookingTimeAsc(status);
    }
    public List<Booking> getBookingsByDate(LocalDate bookingDate) {
        return bookingRepository.findByBookingDate(bookingDate);
    }

    public BookingService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            ServiceRepository serviceRepository) {

        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking createBooking(Booking booking) {

        if (booking.getBookingDate() == null) {
            throw new IllegalArgumentException("Booking date is required");
        }

        if (booking.getBookingTime() == null) {
            throw new IllegalArgumentException("Booking time is required");
        }

        if (booking.getUser() == null || booking.getUser().getId() == null) {
            throw new IllegalArgumentException("User is required");
        }

        com.fixly.fixlybackend.model.User existingUser =
                userRepository.findById(booking.getUser().getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException("User not found"));

        booking.setUser(existingUser);

        if (booking.getService() == null || booking.getService().getId() == null) {
            throw new IllegalArgumentException("Service is required");
        }

        com.fixly.fixlybackend.model.Service existingService =
                serviceRepository.findById(booking.getService().getId())
                        .orElseThrow(() ->
                                new IllegalArgumentException("Service not found"));

        booking.setService(existingService);

        if (bookingRepository.existsByServiceIdAndBookingDateAndBookingTimeAndStatusNot(
                booking.getService().getId(),
                booking.getBookingDate(),
                booking.getBookingTime(),
                BookingStatus.CANCELLED)) {

            throw new IllegalArgumentException(
                    "Service is already booked for this date and time"
            );
        }

        if (booking.getBookingDate().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Booking date cannot be in the past"
            );
        }

        if (booking.getBookingDate().isEqual(java.time.LocalDate.now())
                && booking.getBookingTime().isBefore(java.time.LocalTime.now())) {

            throw new IllegalArgumentException(
                    "Booking time cannot be in the past"
            );
        }

        if (booking.getAddress() == null || booking.getAddress().isBlank()) {
            throw new IllegalArgumentException("Address is required");
        }

        booking.setStatus(BookingStatus.PENDING);

        return bookingRepository.save(booking);

    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Booking not found"));
    }

    public void deleteBooking(Long id) {

        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Booking not found"));

        if (existingBooking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Completed bookings cannot be deleted"
            );
        }
        if (existingBooking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Cancelled bookings cannot be deleted"
            );
        }

        bookingRepository.deleteById(id);
    }

    public Booking updateBooking(Long id, Booking booking) {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Booking not found"));

        if (booking.getBookingDate() != null) {

            if (booking.getBookingDate().isBefore(java.time.LocalDate.now())) {
                throw new IllegalArgumentException(
                        "Booking date cannot be in the past"
                );
            }

            existingBooking.setBookingDate(booking.getBookingDate());
        }
        if (existingBooking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Completed bookings cannot be updated"
            );
        }

        if (existingBooking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Cancelled bookings cannot be updated"
            );
        }

        if (booking.getBookingTime() != null) {

            LocalDate dateToCheck = booking.getBookingDate() != null
                    ? booking.getBookingDate()
                    : existingBooking.getBookingDate();

            if (dateToCheck.isEqual(java.time.LocalDate.now())
                    && booking.getBookingTime().isBefore(java.time.LocalTime.now())) {

                throw new IllegalArgumentException(
                        "Booking time cannot be in the past"
                );
            }

            existingBooking.setBookingTime(booking.getBookingTime());
        }

        if (booking.getAddress() != null && !booking.getAddress().isBlank()) {
            existingBooking.setAddress(booking.getAddress());
        }
        if (bookingRepository
                .existsByServiceIdAndBookingDateAndBookingTimeAndStatusNotAndIdNot(
                        existingBooking.getService().getId(),
                        existingBooking.getBookingDate(),
                        existingBooking.getBookingTime(),
                        BookingStatus.CANCELLED,
                        existingBooking.getId())) {

            throw new IllegalArgumentException(
                    "Service is already booked for this date and time"
            );
        }

        return bookingRepository.save(existingBooking);
    }

    public Booking cancelBooking(Long id) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Completed bookings cannot be cancelled"
            );
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Booking is already cancelled"
            );
        }

        booking.setStatus(BookingStatus.CANCELLED);

        return bookingRepository.save(booking);
    }

    public Booking confirmBooking(Long id) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Cancelled bookings cannot be confirmed"
            );
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "Completed bookings cannot be confirmed"
            );
        }

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException(
                    "Booking is already confirmed"
            );
        }

        booking.setStatus(BookingStatus.CONFIRMED);

        return bookingRepository.save(booking);
    }

    public Booking completeBooking(Long id) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException(
                    "Only confirmed bookings can be completed"
            );
        }

        booking.setStatus(BookingStatus.COMPLETED);

        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsBetweenDates(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date"
            );
        }

        return bookingRepository
                .findByBookingDateBetweenOrderByBookingDateAscBookingTimeAsc(
                        startDate,
                        endDate
                );
    }

    public List<Booking> getBookingsByUserId(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }

        return bookingRepository
                .findByUserIdOrderByBookingDateAscBookingTimeAsc(userId);
    }

    public List<Booking> getBookingsByServiceId(Long serviceId) {

        if (!serviceRepository.existsById(serviceId)) {
            throw new IllegalArgumentException("Service not found");
        }

        return bookingRepository
                .findByServiceIdOrderByBookingDateAscBookingTimeAsc(serviceId);
    }

    public boolean isBookingOwner(Long bookingId, String email) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Booking not found"));

        return booking.getUser()
                .getEmail()
                .equalsIgnoreCase(email);
    }

    public boolean isUserAccountOwner(Long userId, String email) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        return user.getEmail().equalsIgnoreCase(email);
    }
    public BookingResponse toBookingResponse(Booking booking) {

        return new BookingResponse(
                booking.getId(),
                booking.getBookingDate(),
                booking.getBookingTime(),
                booking.getAddress(),
                booking.getStatus(),
                booking.getUser().getId(),
                booking.getUser().getName(),
                booking.getService().getId(),
                booking.getService().getName(),
                booking.getService().getPrice()
        );
    }

}