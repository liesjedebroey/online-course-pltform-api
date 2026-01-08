-- 1. Verwijder oude tabellen als ze bestaan (om fouten te voorkomen)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS refresh_tokens, enrollments, courses, users;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) UNIQUE NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role ENUM('admin', 'instructor', 'student') NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE courses (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         description TEXT,
                         instructor_id BIGINT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         CONSTRAINT fk_instructor FOREIGN KEY (instructor_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE enrollments (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             student_id BIGINT NOT NULL,
                             course_id BIGINT NOT NULL,
                             enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT fk_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
                             CONSTRAINT fk_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

CREATE TABLE refresh_tokens (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                token VARCHAR(255) UNIQUE NOT NULL,
                                expiry_date DATETIME NOT NULL,
                                user_id BIGINT NOT NULL,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                CONSTRAINT fk_user_token FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);