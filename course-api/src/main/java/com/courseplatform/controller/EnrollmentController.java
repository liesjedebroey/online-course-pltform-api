package com.courseplatform.controller;

import com.courseplatform.dto.EnrollmentResponse;
import com.courseplatform.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    // STUDENT or ADMIN can enroll in a course
    @PostMapping("/api/courses/{id}/enroll")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<EnrollmentResponse> enrollInCourse(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.enrollStudent(id));
    }

    // STUDENT can view their own enrollments
    @GetMapping("/api/enrollments/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments() {
        return ResponseEntity.ok(enrollmentService.getMyEnrollments());
    }

    // INSTRUCTOR can view enrollments for their courses
    @GetMapping("/api/instructor/enrollments")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<EnrollmentResponse>> getInstructorEnrollments() {
        return ResponseEntity.ok(enrollmentService.getInstructorEnrollments());
    }

    // ADMIN can view all enrollments
    @GetMapping("/api/admin/enrollments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EnrollmentResponse>> getAllEnrollments() {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }

    // STUDENT (own enrollment) or ADMIN can cancel enrollment
    @DeleteMapping("/api/enrollments/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<Void> cancelEnrollment(@PathVariable Long id) {
        enrollmentService.cancelEnrollment(id);
        return ResponseEntity.noContent().build();
    }
}