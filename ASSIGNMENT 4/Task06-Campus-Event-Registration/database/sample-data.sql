-- Sample Data for Campus Event Registration System
USE campuseventdb;

-- Insert Events
INSERT INTO events (title, category, event_date, venue, capacity) VALUES
('National AI & Robotics Hackathon 2026', 'Technical', '2026-11-25 09:00 AM', 'Tech Park Auditorium', 150),
('Full-Stack Cloud Development Workshop', 'Technical', '2026-11-28 10:00 AM', 'Computer Lab 3', 80),
('Inter-Collegiate Cultural Fiesta (Vibrance)', 'Cultural', '2026-12-05 05:00 PM', 'Main Campus Amphitheatre', 500),
('National Cyber Security Conclave', 'Symposium', '2026-12-10 09:30 AM', 'Convention Center Hall A', 200),
('Campus Entrepreneurship & Startup Summit', 'Management', '2026-12-14 02:00 PM', 'MBA Seminar Hall', 120),
('Annual Sports Meet & Athletics Championship', 'Sports', '2026-12-20 07:00 AM', 'University Sports Arena', 350);

-- Insert Sample Students
INSERT INTO students (roll_no, name, email, department, academic_year) VALUES
('192311094', 'Kattiripalli Dhanush', 'dhanush@simats.edu', 'Computer Science & Engg', 3),
('192311082', 'Priya Sharma', 'priya.s@simats.edu', 'Information Technology', 3),
('192311045', 'Rahul Verma', 'rahul.v@simats.edu', 'Electronics & Comm Engg', 2),
('192311012', 'Ananya Iyer', 'ananya.i@simats.edu', 'Computer Science & Engg', 4),
('192311067', 'Karthik Raja', 'karthik.r@simats.edu', 'Mechanical Engineering', 3);

-- Insert Sample Registrations (Pre-registered)
INSERT INTO registrations (student_id, event_id, status) VALUES
(1, 1, 'CONFIRMED'),
(2, 1, 'CONFIRMED'),
(3, 2, 'CONFIRMED'),
(4, 3, 'CONFIRMED'),
(5, 4, 'CONFIRMED');