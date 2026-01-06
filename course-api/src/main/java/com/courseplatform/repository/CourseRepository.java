package com.courseplatform.repository;

import com.courseplatform.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping
public interface CourseRepository extends JpaRepository<Course, Long> {
    //Required to list courses taught by a specific instructor
    List<Course> findByInstructorId(Long instructorId);
}
