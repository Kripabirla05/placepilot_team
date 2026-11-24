-- Sample data for demo.   Run:  mysql -u root -p placepilot_db < sql/sample_data.sql
USE placepilot_db;

INSERT INTO student (name, email, password, branch, cgpa, backlogs, skills) VALUES
('Aman Sharma',  'aman@gmail.com',  'pass123', 'CSE', 8.5, 0, 'Java,SQL,DSA'),
('Riya Verma',   'riya@gmail.com',  'pass123', 'IT',  7.8, 0, 'Python,SQL,HTML'),
('Karan Singh',  'karan@gmail.com', 'pass123', 'ECE', 6.9, 1, 'C,C++'),
('Neha Gupta',   'neha@gmail.com',  'pass123', 'CSE', 9.1, 0, 'Java,Spring,SQL,React'),
('Rohit Kumar',  'rohit@gmail.com', 'pass123', 'ME',  6.2, 2, 'AutoCAD');

INSERT INTO company (name, location, verified) VALUES
('TCS',      'Mumbai',    1),
('Infosys',  'Bangalore', 1),
('Wipro',    'Pune',      1),
('StartupX', 'Delhi',     0);

INSERT INTO recruiter (name, email, password, company_id) VALUES
('TCS HR',      'hr@tcs.com',      'tcs123',   1),
('Infosys HR',  'hr@infosys.com',  'infy123',  2),
('Wipro HR',    'hr@wipro.com',    'wipro123', 3),
('StartupX HR', 'hr@startupx.com', 'start123', 4);

INSERT INTO job (company_id, role, location, lpa, min_cgpa, required_skills, job_type, probation_months) VALUES
(1, 'Software Engineer',  'Mumbai',    7.0, 7.0, 'Java,SQL,DSA',        'Permanent', 6),
(1, 'System Analyst',     'Pune',      5.5, 6.5, 'SQL,Python',          'Permanent', 3),
(2, 'Full Stack Developer','Bangalore',9.0, 8.0, 'Java,React,SQL',      'Permanent', 6),
(2, 'Data Analyst',       'Bangalore', 6.0, 7.0, 'Python,SQL',          'Contract',  0),
(3, 'Backend Developer',  'Pune',      6.5, 7.0, 'Java,Spring,SQL',     'Permanent', 3),
(3, 'Support Engineer',   'Noida',     3.6, 6.0, 'SQL',                 'Contract',  0);

INSERT INTO internship (company_id, role, min_cgpa, required_skills, paid, stipend, duration_months) VALUES
(1, 'Java Intern',        7.0, 'Java,SQL',     1, 15000, 6),
(2, 'Data Science Intern',7.0, 'Python,SQL',   1, 20000, 3),
(3, 'Web Dev Intern',     6.0, 'HTML,JavaScript', 0, 0,   2);

-- Some existing applications and placements for analytics demo
INSERT INTO application (student_id, job_id, internship_id, status, applied_date) VALUES
(1, 1, NULL, 'Selected',    CURDATE()),
(2, NULL, 2, 'Selected',    CURDATE()),
(4, 3, NULL, 'Shortlisted', CURDATE());

INSERT INTO placement (student_id, job_id, internship_id, package_lpa, ppo_status) VALUES
(1, 1, NULL, 7.0, 'N/A'),
(2, NULL, 2, 0,   'Pending');

INSERT INTO test_result (student_id, test_type, score, total, test_date) VALUES
(1, 'MCQ', 4, 5, CURDATE()),
(4, 'MCQ', 5, 5, CURDATE());
