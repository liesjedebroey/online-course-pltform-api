package com.courseplatform.service;

import com.courseplatform.dto.*;
import com.courseplatform.exception.ResourceNotFoundException;
import com.courseplatform.exception.UnauthorizedActionException;
import com.courseplatform.model.Course;
import com.courseplatform.model.User;
import com.courseplatform.repository.CourseRepository;
import com.courseplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return mapToCourseResponse(course);
    }

    public CourseResponse createCourse(CourseRequest request) {
        String currentUsername = getCurrentUsername();
        User instructor = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUsername));

        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setInstructor(instructor);

        Course savedCourse = courseRepository.save(course);
        return mapToCourseResponse(savedCourse);
    }

    public CourseResponse updateCourseById(Long id, CourseRequest request) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        String currentUsername = getCurrentUsername();
        boolean isAdmin = isCurrentUserAdmin();

        // Check authorization: only owner or admin can update
        if (!isAdmin && !existingCourse.getInstructor().getUsername().equals(currentUsername)) {
            throw new UnauthorizedActionException("You can only update your own courses!");
        }

        existingCourse.setTitle(request.getTitle());
        existingCourse.setDescription(request.getDescription());

        Course updatedCourse = courseRepository.save(existingCourse);
        return mapToCourseResponse(updatedCourse);
    }

    public void deleteCourseById(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    // Helper methods
    private CourseResponse mapToCourseResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getInstructor().getUsername(),
                course.getInstructor().getId(),
                course.getCreatedAt(),
                course.getUpdatedAt()
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
