package com.quizserver.service.user;

import com.quizserver.dto.ChangePasswordRequest;
import com.quizserver.dto.SignupRequest;
import com.quizserver.dto.UserDTO;
import com.quizserver.enteties.TestResult;
import com.quizserver.enteties.User;
import com.quizserver.enums.UserRole;
import com.quizserver.repository.TestResultRepository;
import com.quizserver.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestResultRepository testResultRepository;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    @PostConstruct
    private void createAdminUser() {
        User admin = userRepository.findByRole(UserRole.ADMIN);
        if (admin == null) {
            admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@gmail.com");
            admin.setRole(UserRole.ADMIN);
        }
        // Force sync password to the new standard
        admin.setPassword("Admin@123");
        userRepository.save(admin);
    }

    public Boolean hasUserWithEmail(String email) {
        return userRepository.findFirstByEmail(email) != null;
    }

    public User createUser(SignupRequest request) {
        // --- DEBUG LOGGING ---
        System.out.println("Received Sign-up Request:");
        System.out.println("Email: " + request.getEmail());
        System.out.println("Password: " + (request.getPassword() != null ? "[PROTECTED]" : "null"));
        System.out.println("Confirm Password: " + (request.getConfirmPassword() != null ? "[PROTECTED]" : "null"));
        // ---------------------

        // 1. Email validation
        if (!Pattern.compile(EMAIL_REGEX).matcher(request.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        // 2. Password complexity check
        if (!Pattern.compile(PASSWORD_REGEX).matcher(request.getPassword()).matches()) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters long, include an uppercase letter, a lowercase letter, a number, and a special character.");
        }

        // 3. Passwords match check (with null safety)
        if (request.getConfirmPassword() == null) {
            throw new IllegalArgumentException("Please confirm your password.");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and confirm password do not match.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setAge(request.getAge());
        user.setRole(UserRole.USER);

        return userRepository.save(user);
    }

    public User login(User user) {
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isPresent() && user.getPassword().equals(optionalUser.get().getPassword())) {
            return optionalUser.get();
        }
        return null;
    }

    @Override
    public UserDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<TestResult> results = testResultRepository.findAllByUserId(userId);

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setAge(user.getAge());
        dto.setRole(user.getRole());

        dto.setTestsTaken(results.size());
        dto.setAverageScore(results.stream()
                .mapToDouble(TestResult::getPercentage)
                .average()
                .orElse(0.0));

        dto.setTestResults(results.stream()
                .map(TestResult::getDto)
                .toList());

        return dto;
    }

    @Override
    @Transactional
    public UserDTO updateProfile(Long userId, UserDTO updates) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (updates.getName() != null)
            user.setName(updates.getName());
        if (updates.getAge() != null)
            user.setAge(updates.getAge());

        userRepository.save(user);
        return getUserProfile(userId);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!user.getPassword().equals(request.getOldPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        if (!Pattern.compile(PASSWORD_REGEX).matcher(request.getNewPassword()).matches()) {
            throw new IllegalArgumentException("New password does not meet complexity requirements.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New passwords do not match.");
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);
    }
}
