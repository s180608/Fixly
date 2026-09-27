package com.fixly.fixlybackend.service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.fixly.fixlybackend.model.User;
import com.fixly.fixlybackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.fixly.fixlybackend.repository.BookingRepository;
import com.fixly.fixlybackend.model.UserRole;
import com.fixly.fixlybackend.dto.UserResponse;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
    }

    public UserService(
            UserRepository userRepository,
            BookingRepository bookingRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public User updateUser(Long id, User user) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        if (user.getEmail() != null) {
            throw new IllegalArgumentException(
                    "Email cannot be changed through profile update"
            );
        }

        if (user.getName() != null && !user.getName().isBlank()) {
            existingUser.setName(user.getName());
        }

        if (user.getPhone() != null && !user.getPhone().isBlank()) {
            existingUser.setPhone(user.getPhone());
        }


        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }

        if (bookingRepository.existsByUserId(id)) {
            throw new IllegalArgumentException(
                    "Cannot delete user because they have existing bookings"
            );
        }

        userRepository.deleteById(id);
    }



    public User createUser(User user) {

        if (user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (!user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (user.getPhone() == null || user.getPhone().isBlank()) {
            throw new IllegalArgumentException("Phone number is required");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (user.getPassword().length() < 8) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters"
            );
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setRole(UserRole.CUSTOMER);

        return userRepository.save(user);
    }
    public User login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return user;
    }
    public boolean isUserAccountOwner(Long userId, String email) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        return user.getEmail().equalsIgnoreCase(email);
    }
    public UserResponse toUserResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }
}