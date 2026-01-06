package com.courseplatform.service;

import com.courseplatform.exception.DuplicateEnrollmentException;
import com.courseplatform.model.Enrollment;
import com.courseplatform.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    public Enrollment enrollStudent(Enrollment enrollment){
        //Requirement: check if student is already enrolled in this course
        boolean alreadyEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(
                enrollment.getStudent().getId(),
                enrollment.getCourse().getId()
        );

        if(alreadyEnrolled){
            throw new DuplicateEnrollmentException("Student is already enrolled in this course. (enrollmentId:" + enrollment.getId() + ")" );
        }

        return enrollmentRepository.save(enrollment);
    }
}
