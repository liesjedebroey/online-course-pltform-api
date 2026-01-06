package com.courseplatform.courseapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDateTime enrollmentDate;

    @PrePersist
    protected void onCreate() {
        this.enrollmentDate = LocalDateTime.now();
    }
}