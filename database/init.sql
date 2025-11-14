-- Create Database
CREATE DATABASE IF NOT EXISTS training_management_db;
USE training_management_db;

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS trainees;
DROP TABLE IF EXISTS batches;
DROP TABLE IF EXISTS trainers;

-- Create Trainers Table
CREATE TABLE trainers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(100) NOT NULL,
    available_date DATE,
    time_slot VARCHAR(50),
    assigned BOOLEAN DEFAULT FALSE,
    batch_id BIGINT,
    INDEX idx_location (location),
    INDEX idx_assigned (assigned)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Batches Table
CREATE TABLE batches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(255) NOT NULL,
    location VARCHAR(100) NOT NULL,
    start_date DATE,
    end_date DATE,
    trainer_id BIGINT,
    trainer_name VARCHAR(255),
    max_capacity INT DEFAULT 30,
    enrolled_count INT DEFAULT 0,
    status VARCHAR(50) DEFAULT 'Active',
    INDEX idx_location (location),
    INDEX idx_trainer (trainer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Trainees Table
CREATE TABLE trainees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    batch_id BIGINT,
    batch_name VARCHAR(255),
    location VARCHAR(100),
    INDEX idx_batch (batch_id),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Attendance Table
CREATE TABLE attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trainee_id BIGINT NOT NULL,
    trainee_name VARCHAR(255),
    batch_id BIGINT NOT NULL,
    batch_name VARCHAR(255),
    date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    INDEX idx_trainee (trainee_id),
    INDEX idx_batch (batch_id),
    INDEX idx_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Courses Table
CREATE TABLE IF NOT EXISTS courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    duration_weeks INT,
    category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert Sample Trainers
INSERT INTO trainers (name, location, available_date, time_slot, assigned, batch_id) VALUES
('Rajesh Kumar', 'Mumbai', '2025-01-15', '09:00-18:00', FALSE, NULL),
('Priya Sharma', 'Delhi', '2025-01-16', '09:00-12:00', TRUE, 1),
('Amit Patel', 'Bangalore', '2025-01-17', '12:00-18:00', TRUE, 2),
('Sneha Reddy', 'Hyderabad', '2025-01-18', '09:00-18:00', FALSE, NULL),
('Vikram Singh', 'Chennai', '2025-01-19', '09:00-12:00', TRUE, 3),
('Anjali Mehta', 'Pune', '2025-01-20', '12:00-18:00', FALSE, NULL);

-- Insert Sample Batches
INSERT INTO batches (course_name, location, start_date, end_date, trainer_id, trainer_name, max_capacity, enrolled_count, status) VALUES
('Java Full Stack Development', 'Mumbai', '2025-02-01', '2025-05-31', 2, 'Priya Sharma', 30, 25, 'Active'),
('Python for Data Science', 'Delhi', '2025-02-05', '2025-04-30', 3, 'Amit Patel', 25, 20, 'Active'),
('React & Node.js', 'Bangalore', '2025-02-10', '2025-05-10', 5, 'Vikram Singh', 30, 28, 'Active'),
('Angular Development', 'Hyderabad', '2025-02-15', '2025-05-15', NULL, 'TBD', 20, 15, 'Upcoming'),
('DevOps Engineering', 'Chennai', '2025-03-01', '2025-06-01', NULL, 'TBD', 25, 10, 'Upcoming');

-- Insert Sample Trainees
INSERT INTO trainees (name, email, phone, batch_id, batch_name, location) VALUES
('Rahul Verma', 'rahul.verma@example.com', '9876543210', 1, 'Java Full Stack Development', 'Mumbai'),
('Pooja Gupta', 'pooja.gupta@example.com', '9876543211', 1, 'Java Full Stack Development', 'Mumbai'),
('Arjun Nair', 'arjun.nair@example.com', '9876543212', 2, 'Python for Data Science', 'Delhi'),
('Divya Iyer', 'divya.iyer@example.com', '9876543213', 2, 'Python for Data Science', 'Delhi'),
('Karan Malhotra', 'karan.malhotra@example.com', '9876543214', 3, 'React & Node.js', 'Bangalore'),
('Neha Kapoor', 'neha.kapoor@example.com', '9876543215', 3, 'React & Node.js', 'Bangalore'),
('Rohan Desai', 'rohan.desai@example.com', '9876543216', 4, 'Angular Development', 'Hyderabad'),
('Shreya Joshi', 'shreya.joshi@example.com', '9876543217', 4, 'Angular Development', 'Hyderabad'),
('Aditya Rao', 'aditya.rao@example.com', '9876543218', 5, 'DevOps Engineering', 'Chennai'),
('Meera Nambiar', 'meera.nambiar@example.com', '9876543219', 5, 'DevOps Engineering', 'Chennai');

-- Insert Sample Attendance Records
INSERT INTO attendance (trainee_id, trainee_name, batch_id, batch_name, date, status) VALUES
(1, 'Rahul Verma', 1, 'Java Full Stack Development', '2025-02-01', 'PRESENT'),
(2, 'Pooja Gupta', 1, 'Java Full Stack Development', '2025-02-01', 'PRESENT'),
(3, 'Arjun Nair', 2, 'Python for Data Science', '2025-02-05', 'LATE'),
(4, 'Divya Iyer', 2, 'Python for Data Science', '2025-02-05', 'PRESENT'),
(5, 'Karan Malhotra', 3, 'React & Node.js', '2025-02-10', 'PRESENT'),
(6, 'Neha Kapoor', 3, 'React & Node.js', '2025-02-10', 'ABSENT'),
(1, 'Rahul Verma', 1, 'Java Full Stack Development', '2025-02-02', 'PRESENT'),
(2, 'Pooja Gupta', 1, 'Java Full Stack Development', '2025-02-02', 'PRESENT');

-- Insert Sample Courses
INSERT INTO courses (course_name, description, duration_weeks, category) VALUES
('Java Full Stack Development', 'Complete Java full stack development with Spring Boot and React', 16, 'Programming'),
('Python for Data Science', 'Data analysis and machine learning with Python', 12, 'Data Science'),
('React & Node.js', 'Modern web development with MERN stack', 14, 'Web Development'),
('DevOps Engineering', 'CI/CD, Docker, Kubernetes and cloud platforms', 10, 'DevOps'),
('Angular Development', 'Enterprise application development with Angular', 12, 'Web Development'),
('Vue.js Development', 'Progressive web apps with Vue.js', 10, 'Web Development'),
('AWS Cloud Computing', 'Amazon Web Services certification preparation', 8, 'Cloud Computing'),
('Machine Learning', 'Deep learning and neural networks', 16, 'Data Science'),
('Mobile App Development', 'iOS and Android app development', 14, 'Mobile Development'),
('Database Management', 'SQL, NoSQL and database administration', 8, 'Database');

-- Verify Data
SELECT 'Trainers Count:' AS Info, COUNT(*) AS Count FROM trainers
UNION ALL
SELECT 'Batches Count:', COUNT(*) FROM batches
UNION ALL
SELECT 'Trainees Count:', COUNT(*) FROM trainees
UNION ALL
SELECT 'Attendance Records:', COUNT(*) FROM attendance;