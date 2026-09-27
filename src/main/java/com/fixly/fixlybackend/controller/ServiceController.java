package com.fixly.fixlybackend.controller;
import com.fixly.fixlybackend.model.ServiceCategory;
import com.fixly.fixlybackend.model.Service;
import com.fixly.fixlybackend.service.ServiceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.fixly.fixlybackend.dto.ServiceResponse;
@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    public List<ServiceResponse> getAllServices() {

        return serviceService.getAllServices()
                .stream()
                .map(serviceService::toServiceResponse)
                .toList();
    }

    @PostMapping
    public ServiceResponse createService(
            @RequestBody Service service) {

        Service createdService =
                serviceService.createService(service);

        return serviceService.toServiceResponse(createdService);
    }

    @PutMapping("/{id}")
    public ServiceResponse updateService(
            @PathVariable Long id,
            @RequestBody Service service) {

        Service updatedService =
                serviceService.updateService(id, service);

        return serviceService.toServiceResponse(updatedService);
    }

    @DeleteMapping("/{id}")
    public void deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
    }

    @GetMapping("/{id}")
    public ServiceResponse getServiceById(
            @PathVariable Long id) {

        Service service =
                serviceService.getServiceById(id);

        return serviceService.toServiceResponse(service);
    }

    @GetMapping("/category/{category}")
    public List<ServiceResponse> getServicesByCategory(
            @PathVariable ServiceCategory category) {

        return serviceService.getServicesByCategory(category)
                .stream()
                .map(serviceService::toServiceResponse)
                .toList();
    }

    @GetMapping("/search")
    public List<ServiceResponse> searchServices(
            @RequestParam String name) {

        return serviceService.searchServicesByName(name)
                .stream()
                .map(serviceService::toServiceResponse)
                .toList();
    }
}