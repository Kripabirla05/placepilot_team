-- PlacePilot database schema
-- Run:  mysql -u root -p < sql/schema.sql
DROP DATABASE IF EXISTS placepilot_db;
CREATE DATABASE placepilot_db;
USE placepilot_db;

CREATE TABLE admin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL
);

CREATE TABLE student (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    branch VARCHAR(30) NOT NULL,
    cgpa DOUBLE NOT NULL DEFAULT 0,
    backlogs INT NOT NULL DEFAULT 0,
    skills VARCHAR(255) DEFAULT ''
);

CREATE TABLE company (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    location VARCHAR(100) NOT NULL,
    verified TINYINT(1) NOT NULL DEFAULT 0
);

CREATE TABLE recruiter (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    company_id INT NOT NULL,
    FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE
);

CREATE TABLE job (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT NOT NULL,
    role VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    lpa DOUBLE NOT NULL,
    min_cgpa DOUBLE NOT NULL DEFAULT 0,
    required_skills VARCHAR(255) DEFAULT '',
    job_type VARCHAR(20) NOT NULL DEFAULT 'Permanent',
    probation_months INT NOT NULL DEFAULT 0,
    FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE
);

CREATE TABLE internship (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT NOT NULL,
    role VARCHAR(100) NOT NULL,
    min_cgpa DOUBLE NOT NULL DEFAULT 0,
    required_skills VARCHAR(255) DEFAULT '',
    paid TINYINT(1) NOT NULL DEFAULT 1,
    stipend DOUBLE NOT NULL DEFAULT 0,
    duration_months INT NOT NULL,
    FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE
);

-- One application is for a job OR an internship (the other column is NULL)
CREATE TABLE application (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    job_id INT NULL,
    internship_id INT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Applied',
    applied_date DATE NOT NULL,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (job_id) REFERENCES job(id) ON DELETE CASCADE,
    FOREIGN KEY (internship_id) REFERENCES internship(id) ON DELETE CASCADE
);

CREATE TABLE test_result (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    test_type VARCHAR(30) NOT NULL,
    score INT NOT NULL,
    total INT NOT NULL,
    test_date DATE NOT NULL,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
);

CREATE TABLE notification (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    message VARCHAR(255) NOT NULL,
    is_read TINYINT(1) NOT NULL DEFAULT 0,
    priority INT NOT NULL DEFAULT 2,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
);

CREATE TABLE placement (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    job_id INT NULL,
    internship_id INT NULL,
    package_lpa DOUBLE NOT NULL DEFAULT 0,
    ppo_status VARCHAR(20) NOT NULL DEFAULT 'N/A',
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (job_id) REFERENCES job(id) ON DELETE SET NULL,
    FOREIGN KEY (internship_id) REFERENCES internship(id) ON DELETE SET NULL
);

INSERT INTO admin (username, password) VALUES ('admin', 'admin123');
