package com.courseplatform.repository;

import com.courseplatform.model.Course;
import com.courseplatform.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment,Long>{
    //Used for students to see their own course enrollments
    List<Enrollment> findByStudentId(Long studentId);

    //Used for instructors to se enrollments for their specific courses
    List<Enrollment> findByInstructorId(Long instructorId);

    //Important: check for duplicate enrollments before saving
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

}
