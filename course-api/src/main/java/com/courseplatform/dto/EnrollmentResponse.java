package com.courseplatform.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseTitle;
    private LocalDateTime enrollmentDate;
}
