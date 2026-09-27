package com.fixly.fixlybackend.service;
import com.fixly.fixlybackend.model.ServiceCategory;
import com.fixly.fixlybackend.model.Service;
import com.fixly.fixlybackend.repository.ServiceRepository;
import com.fixly.fixlybackend.repository.BookingRepository;
import com.fixly.fixlybackend.dto.ServiceResponse;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {



    private final ServiceRepository serviceRepository;
    private final BookingRepository bookingRepository;
    public List<Service> searchServicesByName(String name) {
        return serviceRepository.findByNameContainingIgnoreCase(name);
    }
    public ServiceService(


            ServiceRepository serviceRepository,
            BookingRepository bookingRepository) {



        this.serviceRepository = serviceRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public List<Service> getServicesByCategory(ServiceCategory category) {
        return serviceRepository.findByCategory(category);
    }

    public Service createService(Service service) {
        if (service.getName() == null || service.getName().isBlank()) {
            throw new IllegalArgumentException("Service name is required");
        }
        if (service.getPrice() <= 0) {
            throw new IllegalArgumentException("Service price must be greater than 0");
        }
        if (service.getCategory() == null) {
            throw new IllegalArgumentException("Service category is required");
        }
        if (service.getDescription() == null || service.getDescription().isBlank()) {
            throw new IllegalArgumentException("Service description is required");
        }

        return serviceRepository.save(service);
    }
    public Service updateService(Long id, Service service) {
        Service existingService = serviceRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Service not found"));

        if (service.getName() != null && !service.getName().isBlank()) {
            existingService.setName(service.getName());
        }
        if (service.getDescription() != null && !service.getDescription().isBlank()) {
            existingService.setDescription(service.getDescription());
        }
        if (service.getPrice() > 0) {
            existingService.setPrice(service.getPrice());
        }

        if (service.getCategory() != null) {
            existingService.setCategory(service.getCategory());
        }

        return serviceRepository.save(existingService);
    }

    public void deleteService(Long id) {

        if (!serviceRepository.existsById(id)) {
            throw new IllegalArgumentException("Service not found");
        }

        if (bookingRepository.existsByServiceId(id)) {
            throw new IllegalArgumentException(
                    "Cannot delete service because it has existing bookings"
            );
        }

        serviceRepository.deleteById(id);
    }

    public Service getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Service not found"));
    }
    public ServiceResponse toServiceResponse(
            com.fixly.fixlybackend.model.Service service) {

        return new ServiceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getPrice(),
                service.getCategory()
        );
    }
}