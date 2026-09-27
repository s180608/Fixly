package com.fixly.fixlybackend.repository;
import com.fixly.fixlybackend.model.ServiceCategory;
import com.fixly.fixlybackend.model.Service;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByCategory(ServiceCategory category);
    List<Service> findByNameContainingIgnoreCase(String name);
}