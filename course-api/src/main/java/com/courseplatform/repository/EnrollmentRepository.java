package com.courseplatform.repository;

import com.courseplatform.model.Course;
import com.courseplatform.model.Enrollment;
import com.courseplatform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment,Long>{
    //Used for students to see their own course enrollments
    List<Enrollment> findByStudent_Id(Long studentId);

    //Used for instructors to se enrollments for their specific courses
    List<Enrollment> findByCourse_Instructor_Id(Long instructorId);

    //Important: check for duplicate enrollments before saving
    boolean existsByStudent_IdAndCourse_Id(Long studentId, Long courseId);

}
