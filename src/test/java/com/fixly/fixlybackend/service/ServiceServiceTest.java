package com.fixly.fixlybackend.service;

import com.fixly.fixlybackend.repository.BookingRepository;
import com.fixly.fixlybackend.repository.ServiceRepository;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fixly.fixlybackend.model.Service;
import com.fixly.fixlybackend.model.ServiceCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ServiceServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ServiceService serviceService;

    @Test
    void createServiceShouldSaveValidService() {

        Service service = new Service();
        service.setName("Plumbing");
        service.setDescription("General plumbing repairs");
        service.setPrice(55.0);
        service.setCategory(ServiceCategory.PLUMBING);

        when(serviceRepository.save(any(Service.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Service savedService =
                serviceService.createService(service);

        assertEquals("Plumbing", savedService.getName());
        assertEquals(55.0, savedService.getPrice());
        assertEquals(
                ServiceCategory.PLUMBING,
                savedService.getCategory()
        );
    }

    @Test
    void createServiceShouldRejectInvalidPrice() {

        Service service = new Service();
        service.setName("Plumbing");
        service.setDescription("General plumbing repairs");
        service.setPrice(0);
        service.setCategory(ServiceCategory.PLUMBING);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> serviceService.createService(service)
                );

        assertEquals(
                "Service price must be greater than 0",
                exception.getMessage()
        );
    }

    @Test
    void deleteServiceShouldRejectServiceWithBookings() {

        when(serviceRepository.existsById(1L))
                .thenReturn(true);

        when(bookingRepository.existsByServiceId(1L))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> serviceService.deleteService(1L)
                );

        assertEquals(
                "Cannot delete service because it has existing bookings",
                exception.getMessage()
        );
    }
}