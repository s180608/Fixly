package com.fixly.fixlybackend.repository;
import java.util.Optional;
import com.fixly.fixlybackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
}