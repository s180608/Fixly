package com.fixly.fixlybackend.dto;

import com.fixly.fixlybackend.model.BookingStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingResponse {

    private Long id;
    private LocalDate bookingDate;
    private LocalTime bookingTime;
    private String address;
    private BookingStatus status;

    private Long userId;
    private String userName;

    private Long serviceId;
    private String serviceName;
    private double servicePrice;

    public BookingResponse(
            Long id,
            LocalDate bookingDate,
            LocalTime bookingTime,
            String address,
            BookingStatus status,
            Long userId,
            String userName,
            Long serviceId,
            String serviceName,
            double servicePrice) {

        this.id = id;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.address = address;
        this.status = status;
        this.userId = userId;
        this.userName = userName;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public LocalTime getBookingTime() {
        return bookingTime;
    }

    public String getAddress() {
        return address;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getServicePrice() {
        return servicePrice;
    }
}
