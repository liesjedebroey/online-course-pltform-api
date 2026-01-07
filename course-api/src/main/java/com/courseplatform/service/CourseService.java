package com.courseplatform.service;

import com.courseplatform.exception.ResourceNotFoundException;
import com.courseplatform.model.Course;
import com.courseplatform.model.User;
import com.courseplatform.repository.CourseRepository;
import com.courseplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Course createCourse(Course course) {
        // 1. Haal de naam van de ingelogde docent uit de SecurityContext
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Zoek die gebruiker op in de database
        User instructor = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Ingelogde gebruiker niet gevonden: " + currentUsername));

        // 3. Koppel de instructor aan de cursus (DIT VOORKOMT DE NULL ERROR)
        course.setInstructor(instructor);

        // 4. Nu pas opslaan in de database
        return courseRepository.save(course);
    }

    public Course updateCourseById(Long id, Course courseDetails) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        // Check autorisatie: Wie is de ingelogde gebruiker?
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Als het geen admin is EN de naam komt niet overeen met de eigenaar -> Error!
        if (!isAdmin && !existingCourse.getInstructor().getUsername().equals(currentUsername)) {
            throw new RuntimeException("Je mag alleen je eigen cursussen aanpassen!");
        }

        existingCourse.setTitle(courseDetails.getTitle());
        existingCourse.setDescription(courseDetails.getDescription());
        return courseRepository.save(existingCourse);
    }


    public void deleteCourseById(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

}
