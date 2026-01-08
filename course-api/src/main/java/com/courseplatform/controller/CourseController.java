package com.courseplatform.controller;

import com.courseplatform.dto.CourseRequest;
import com.courseplatform.dto.CourseResponse;
import com.courseplatform.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // Public endpoint - anyone can view courses
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // Public endpoint - anyone can view a specific course
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    // Only INSTRUCTOR or ADMIN can create courses
    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<CourseResponse> createCourse(@RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.createCourse(request));
    }

    // INSTRUCTOR (own course) or ADMIN can update
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourseById(id, request));
    }

    // Only ADMIN can delete courses
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourseById(id);
        return ResponseEntity.noContent().build();
    }
}