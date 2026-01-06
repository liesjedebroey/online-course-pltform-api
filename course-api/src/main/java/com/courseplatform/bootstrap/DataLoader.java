package com.courseplatform.bootstrap;

import com.courseplatform.model.*;
import com.courseplatform.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, CourseRepository courseRepository,
                      EnrollmentRepository enrollmentRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            seedData();
        }
    }

    private void seedData() {
        //1 CREATE USERS (WITH HASHING PASSWORDS)
        User admin = createAndSaveUser("admin", "admin@test.com", "admin123", Role.ADMIN);

        User instructor1 = createAndSaveUser("john_instructor", "john@test.com", "password123", Role.INSTRUCTOR);
        User instructor2 = createAndSaveUser("jane_instructor", "jane@test.com", "password123", Role.INSTRUCTOR);


        User student1 = createAndSaveUser("alice_student", "alice@test.com", "password123", Role.STUDENT);
        User student2 = createAndSaveUser("bob_student", "bob@test.com", "password123", Role.STUDENT);
        User student3 = createAndSaveUser("charlie_student", "charlie@test.com", "password123", Role.STUDENT);

        // 2 CREATE COURSES [cite: 146]
        Course javaCourse = createAndSaveCourse("Java Masterclass", "Learn Spring Boot", instructor1);
        Course reactCourse = createAndSaveCourse("React Basics", "Frontend development", instructor2);
        Course sqlCourse = createAndSaveCourse("SQL for Beginners", "Database design", instructor1);

        // 3 CREATE ENROLLMENTS [cite: 146]
        enrollStudent(student1, javaCourse);
        enrollStudent(student2, javaCourse);
        enrollStudent(student1, reactCourse);

        System.out.println("Database seeded successfully! [cite: 169]");
    }

    private User createAndSaveUser(String username, String email, String password, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return userRepository.save(user);
    }

    private Course createAndSaveCourse(String name, String description, User instructor) {
        Course course = new Course();
        course.setTitle(name);
        course.setDescription(description);
        course.setInstructor(instructor);
        return courseRepository.save(course);
    }

    private void enrollStudent(User student, Course course) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollmentRepository.save(enrollment);
    }
}
