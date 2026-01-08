package com.courseplatform.service;

import com.courseplatform.dto.EnrollmentResponse;
import com.courseplatform.exception.DuplicateEnrollmentException;
import com.courseplatform.exception.ResourceNotFoundException;
import com.courseplatform.exception.UnauthorizedActionException;
import com.courseplatform.model.Course;
import com.courseplatform.model.Enrollment;
import com.courseplatform.model.User;
import com.courseplatform.repository.CourseRepository;
import com.courseplatform.repository.EnrollmentRepository;
import com.courseplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public EnrollmentResponse enrollStudent(Long courseId) {
        String currentUsername = getCurrentUsername();
        User student = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Check if already enrolled
        boolean alreadyEnrolled = enrollmentRepository.existsByStudent_IdAndCourse_Id(
                student.getId(), courseId);

        if (alreadyEnrolled) {
            throw new DuplicateEnrollmentException(
                    "You are already enrolled in this course: " + course.getTitle());
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return mapToEnrollmentResponse(savedEnrollment);
    }

    public List<EnrollmentResponse> getMyEnrollments() {
        String currentUsername = getCurrentUsername();
        User student = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        return enrollmentRepository.findByStudent_Id(student.getId()).stream()
                .map(this::mapToEnrollmentResponse)
                .collect(Collectors.toList());
    }

    public List<EnrollmentResponse> getInstructorEnrollments() {
        String currentUsername = getCurrentUsername();
        User instructor = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        return enrollmentRepository.findByCourse_Instructor_Id(instructor.getId()).stream()
                .map(this::mapToEnrollmentResponse)
                .collect(Collectors.toList());
    }

    public List<EnrollmentResponse> getAllEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(this::mapToEnrollmentResponse)
                .collect(Collectors.toList());
    }

    public void cancelEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        String currentUsername = getCurrentUsername();
        boolean isAdmin = isCurrentUserAdmin();

        // Check authorization: only the student themselves or admin can cancel
        if (!isAdmin && !enrollment.getStudent().getUsername().equals(currentUsername)) {
            throw new UnauthorizedActionException("You can only cancel your own enrollments!");
        }

        enrollmentRepository.deleteById(enrollmentId);
    }

    // Helper methods
    private EnrollmentResponse mapToEnrollmentResponse(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudent().getId(),
                enrollment.getStudent().getUsername(),
                enrollment.getCourse().getId(),
                enrollment.getCourse().getTitle(),
                enrollment.getEnrollmentDate()
        );
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private boolean isCurrentUserAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}