package com.courseplatform.service;

import com.courseplatform.dto.CourseRequest;
import com.courseplatform.dto.CourseResponse;
import com.courseplatform.exception.ResourceNotFoundException;
import com.courseplatform.exception.UnauthorizedActionException;
import com.courseplatform.model.Course;
import com.courseplatform.model.Role;
import com.courseplatform.model.User;
import com.courseplatform.repository.CourseRepository;
import com.courseplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CourseService courseService;

    private User instructor;
    private User admin;
    private Course course;

    @BeforeEach
    void setUp() {
        // Setup test data
        instructor = new User();
        instructor.setId(1L);
        instructor.setUsername("john_instructor");
        instructor.setEmail("john@test.com");
        instructor.setRole(Role.INSTRUCTOR);

        admin = new User();
        admin.setId(2L);
        admin.setUsername("admin");
        admin.setEmail("admin@test.com");
        admin.setRole(Role.ADMIN);

        course = new Course();
        course.setId(1L);
        course.setTitle("Java Masterclass");
        course.setDescription("Learn Spring Boot");
        course.setInstructor(instructor);

        // Setup Security Context
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getAllCourses_ShouldReturnAllCourses() {
        // Arrange
        List<Course> courses = Arrays.asList(course);
        when(courseRepository.findAll()).thenReturn(courses);

        // Act
        List<CourseResponse> result = courseService.getAllCourses();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Java Masterclass", result.get(0).getTitle());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void getCourseById_WhenCourseExists_ShouldReturnCourse() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        // Act
        CourseResponse result = courseService.getCourseById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Java Masterclass", result.getTitle());
        assertEquals("john_instructor", result.getInstructorName());
    }

    @Test
    void getCourseById_WhenCourseNotExists_ShouldThrowException() {
        // Arrange
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.getCourseById(999L);
        });
    }

    @Test
    void createCourse_AsInstructor_ShouldCreateCourse() {
        // Arrange
        CourseRequest request = new CourseRequest();
        request.setTitle("New Course");
        request.setDescription("New Description");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("john_instructor");
        when(userRepository.findByUsername("john_instructor")).thenReturn(Optional.of(instructor));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // Act
        CourseResponse result = courseService.createCourse(request);

        // Assert
        assertNotNull(result);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void updateCourseById_AsOwner_ShouldUpdateCourse() {
        // Arrange
        CourseRequest request = new CourseRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("john_instructor");
        when(authentication.getAuthorities()).thenReturn(
                Arrays.asList(new SimpleGrantedAuthority("ROLE_INSTRUCTOR")));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // Act
        CourseResponse result = courseService.updateCourseById(1L, request);

        // Assert
        assertNotNull(result);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void updateCourseById_AsNonOwner_ShouldThrowException() {
        // Arrange
        CourseRequest request = new CourseRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");

        User otherInstructor = new User();
        otherInstructor.setUsername("jane_instructor");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("jane_instructor");
        when(authentication.getAuthorities()).thenReturn(
                Arrays.asList(new SimpleGrantedAuthority("ROLE_INSTRUCTOR")));

        // Act & Assert
        assertThrows(UnauthorizedActionException.class, () -> {
            courseService.updateCourseById(1L, request);
        });
    }

    @Test
    void deleteCourseById_WhenCourseExists_ShouldDeleteCourse() {
        // Arrange
        when(courseRepository.existsById(1L)).thenReturn(true);

        // Act
        courseService.deleteCourseById(1L);

        // Assert
        verify(courseRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCourseById_WhenCourseNotExists_ShouldThrowException() {
        // Arrange
        when(courseRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.deleteCourseById(999L);
        });
    }
}