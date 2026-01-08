package com.courseplatform.service;

import com.courseplatform.dto.EnrollmentResponse;
import com.courseplatform.exception.ResourceNotFoundException;
import com.courseplatform.exception.DuplicateEnrollmentException; // Je eigen exception
import com.courseplatform.model.Course;
import com.courseplatform.model.Enrollment;
import com.courseplatform.model.User;
import com.courseplatform.repository.CourseRepository;
import com.courseplatform.repository.EnrollmentRepository;
import com.courseplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private UserRepository userRepository;

    // Voeg deze twee mocks toe!
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private User student;
    private Course course;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        student = new User();
        student.setId(1L);
        student.setUsername("student_user");

        course = new Course();
        course.setId(10L);
        course.setTitle("Spring Boot Masterclass");

        enrollment = new Enrollment();
        enrollment.setId(100L);
        enrollment.setStudent(student);
        enrollment.setCourse(course);

        // Vertel Spring Security om onze mock securityContext te gebruiken
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void enrollStudent_Success_ShouldReturnResponse() {
        // Arrange
        Long courseId = 10L;

        // Mock de security flow: Context -> Authentication -> Name
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("student_user");

        when(userRepository.findByUsername("student_user")).thenReturn(Optional.of(student));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Zorg dat de methode naam matcht met jouw repository (existsByStudent_IdAndCourse_Id)
        when(enrollmentRepository.existsByStudent_IdAndCourse_Id(1L, 10L)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        // Act
        EnrollmentResponse response = enrollmentService.enrollStudent(courseId);

        // Assert
        assertNotNull(response);
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void enrollStudent_AlreadyEnrolled_ShouldThrowException() {
        // Arrange
        Long courseId = 10L;
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("student_user");

        when(userRepository.findByUsername("student_user")).thenReturn(Optional.of(student));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Simuleer dat student al is ingeschreven
        when(enrollmentRepository.existsByStudent_IdAndCourse_Id(1L, 10L)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateEnrollmentException.class, () -> {
            enrollmentService.enrollStudent(courseId);
        });
    }
}