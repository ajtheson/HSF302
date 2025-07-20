--bd94dcda26fccb4e68d6a31f9b5aac0b571ae266d822620e901ef7ebe3a11d4f
use QuizOnlineSystem
-- Insert Teachers
INSERT INTO teacher_entity(email, password, full_name, school) VALUES
('teacher1@example.com', '9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c', 'Nguyen Van A', 'High School A'),
('teacher2@example.com', '9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c', 'Tran Thi B', 'High School B');

-- Insert Students
INSERT INTO student_entity(email, password, full_name, class_name, school) VALUES
('student1@example.com', '9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c', 'Le Van C', '12A1', 'High School A'),
('student2@example.com', '9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c', 'Pham Thi D', '12A2', 'High School B'),
('student3@example.com', '9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c', 'Nguyen Van E', '12A3', 'High School C'),
('student4@example.com', '9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c', 'Tran Thi F', '12A4', 'High School D'),
('student5@example.com', '9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c', 'Hoang Van G', '12A1', 'High School A');

-- Insert Quizzes
INSERT INTO quiz_entity (quiz_name, quantity, is_deleted, teacher_id) VALUES
('SSLc101', 5, 0, 1),
('PRF192', 5, 0, 2);

-- Insert Questions
INSERT INTO question_entity (content, is_multiple_choice, is_deleted,quiz_id) VALUES
('How is bias displayed in this statement?', 0, 0,1),
('According to the lesson, how long does it take to master communication skills?', 0,0, 1),
('Taking notes during lectures is an effective way that helps students get the most out of their courses. So, what should students note down? (Choose 2)', 1,0, 1),
('Which listeners interrupt whenever they do not understand?', 0,0, 1),
('What is the relationship between critical thinking and communication?', 0,0, 1),
('Who is the inventor of the C programming language?', 0,0, 2),
('How does compilation differ from interpretation in the context of C programming language?', 0,0, 2),
('Errors that occur due to syntax errors belong to which of the following?', 0,0, 2),
('Which of the following statements is incorrect?', 0,0, 2),
('Which of the following accurately describes the scope of a local variable in C?', 0,0, 2);

-- Insert Choices
INSERT INTO choice_entity(choice_content, is_correct_choice, is_deleted,question_id) VALUES
('False classification schemes', 0, 0,1),
('Use of passive voice to hide responsibility', 1, 0,1),
('Ad hominem', 0, 0,1),
('Appealing to membership of a common group', 0, 0,1),
('1 year', 0, 0,2),
('5 years', 0, 0,2),
('2-3 years', 0, 0,2),
('Your whole life', 1, 0,2),
('As much as possible', 0, 0,3),
('Main ideas, Processes, Formulas, Arguments', 1, 0,3),
('All information out of the textbook', 0, 0,3),
('Questions for lecturer/tutor', 1, 0,3),
('Effective listeners', 0, 0, 4),
('Ineffective listeners', 1, 0, 4),
('Empathic listeners', 0, 0, 4),
('Surface listeners', 0, 0, 4),
('Communication is needed to form an argument, and thus display critical thinking', 1, 0, 5),
('These two have no relationship at all', 0, 0, 5),
('Critical thinking helps you win when discussing an issue with friends', 0, 0, 5),
('Communication is critical to thinking', 0, 0, 5),
('Dennis Richie', 1, 0, 6),
('Bjarne Stroustrup', 0, 0, 6),
('Brian Kernighan', 0, 0, 6),
('Niklaus Wirth', 0, 0, 6),
('Compilation involves converting source code to machine code, while interpretation involves executing code line by line', 1, 0, 7),
('Compilation and interpretation are two terms that are used interchangeably', 0, 0, 7),
('Algorithm is another term for interpretation in programming languages', 0, 0, 7),
('IDE tools are responsible for both compilation and interpretation processes', 0, 0, 7),
('Compile time error', 1, 0, 8),
('Run time error', 0, 0, 8),
('Input error', 0, 0, 8),
('Linking error', 0, 0, 8),
('To make our programs longer, we use higher-level languages', 0, 0, 9),
('Program code in a high level language can not run, it must be translated to binary code (machine code)before running', 0, 0, 9),
('C is based on a view of structured programming', 0, 0, 9),
('Programs that perform relatively simple tasks and are written in assembly language contain a large number of statements', 1, 0, 9),
('It is visible only within the function where it is declared', 1, 0, 10),
('It is accessible throughout the entire program', 0, 0, 10),
('It can be accessed by any function within the same source file', 0, 0, 10),
('It is available globally but with restricted modification rights', 0, 0, 10);

-- Insert Exams
INSERT INTO exam_entity (exam_code, exam_name, duration, start_time, end_time, attempts, is_review, quiz_id) VALUES
('12345', 'SSLc101_1', 10,'2025-02-19 08:00:00', '2025-02-19 09:00:00',2, 1, 1),
('23456', 'SSLc101_2', 10,'2025-02-19 10:00:00', '2025-02-19 11:00:00',3, 0, 1),
('34567', 'PRF192_1', 10,'2025-02-19 09:00:00', '2025-02-19 20:00:00',1, 1, 2);

-- Insert Submissions
INSERT INTO submission_entity (submit_time, duration, selected, correct_answers, score, is_submit, student_id, exam_id) VALUES
('2025-02-19 08:50:00', 5, 5, 4, 8.0,1, 1, 1), -- Student 1 _ SSLc101_1    1
('2025-02-19 08:50:00', 5, 5, 3, 6.0,1, 2, 1), -- Student 2 _ SSLc101_1    2
('2025-02-19 08:50:00', 5, 4, 3, 6.0,1, 3, 1), -- Student 3 _ SSLc101_1    3
('2025-02-19 08:50:00', 5, 5, 3, 6.0,1, 4, 1), -- Student 4 _ SSLc101_1    4
('2025-02-19 08:50:00', 5, 3, 2, 4.0,1, 5, 1), -- Student 5 _ SSLc101_1    5
('2025-02-19 10:30:00', 7, 4, 3, 6.0,1, 2, 2), -- Student 2 _ SSLc101_2
('2025-02-19 11:55:00', 6, 5, 3, 6.0,1, 2, 3); -- Student 2 _ PRF192_1


INSERT INTO answer_entity(student_choice, is_correct, question_id, submission_id) VALUES

('2', 1, 1, 1), 
('8', 1, 2, 1), 
('10', 0, 3, 1), 
('14', 1, 4, 1), 
('17', 1, 5, 1), 

('1', 0, 1, 2),  
('8', 1, 2, 2), 
('10 12', 1, 3, 2), 
('13', 0, 4, 2),
('17', 1, 5, 2),

('2', 1, 1, 3),
('', 0, 2, 3),  
('10 11', 0, 3, 3), 
('14', 1, 4, 3), 
('17', 1, 5, 3), 

('1', 0, 1, 4),
('8', 1, 2, 4), 
('10', 0, 3, 4), 
('14', 1, 4, 4), 
('17', 1, 5, 4),

('3', 0, 1, 5), 
('', 0, 2, 5),
('10 12', 1, 3, 5), 
('', 0, 4, 5), 
('17', 1, 5, 5),

-- Student 2 (Submission ID 2) - 3/4
('2', 1, 1, 6),  -- 'Use of passive voice to hide responsibility'
('8', 1, 2, 6),  -- 'Your whole life'
('10', 0, 3, 6), -- 'Main ideas, Processes, Formulas, Arguments' 
('', 0, 4, 6), -- No answer
('17', 1, 5, 6), -- 'Communication is needed to form an argument, and thus display critical thinking'

-- Student 2 (Submission ID 3) - 3/5
('21', 1, 6, 7), -- 'Dennis Richie'
('25', 1, 7, 7), -- 'Compilation involves converting source code to machine code, while interpretation involves executing code line by line'
('31', 0, 8, 7), -- 'Input error' 
('33', 0, 9, 7), -- 'To make our programs longer, we use higher-level languages'
('37', 1, 10, 7); -- 'It is visible only within the function where it is declared'