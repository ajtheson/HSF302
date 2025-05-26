USE master;
ALTER DATABASE QuizOnlineSystem SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
DROP DATABASE QuizOnlineSystem;
use master

create database QuizOnlineSystem
	
go

use QuizOnlineSystem

create table Teacher(
	teacherID int identity primary key,
	email varchar(255),
	password varchar(50),
	fullname varchar(50),
	school varchar(255)
)

create table Student(
	studentID int identity primary key,
	email varchar(255),
	password varchar(50),
	fullname varchar(50),
	className varchar(50),
	school varchar(255)
)

create table Quiz(
	quizID int identity primary key,
	quizName varchar(255),
	quantity int,
	isDeleted bit,
	teacherID int foreign key references Teacher(teacherID),
)

create table Question(
	questionID int identity primary key,
	content varchar(255),
	isMultipleChoice bit,
	isDeleted bit,
	quizID int foreign key references Quiz(quizID)
)

create table Choice(
	choiceID int identity primary key,
	choiceContent varchar(255),
	isCorrectChoice bit,
	isDeleted bit,
	questionID int foreign key references Question(questionID)
)

create table Exam(
	examID int identity primary key,
	examCode int,
	examName varchar(255),
	duration int,
	startTime datetime,
	endTime datetime,
	attempts int,
	isReview bit,
	quizID int foreign key references Quiz(quizID)
)

create table Submission(
	submissionID int identity primary key,
	submitTime datetime,
	duration int,
	selected int,
	correctAnswers int,
	score decimal(3,1),
	isSubmit bit,
	studentID int foreign key references Student(studentID),
	examID int foreign key references Exam(examID)
)

create table Answer(
	answerID int identity primary key,
	studentChoice varchar(255),
	isCorrect bit,
	questionID int foreign key references Question(questionID),
	submissionID int foreign key references Submission(submissionID)
)