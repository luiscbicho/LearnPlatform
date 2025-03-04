# LearnPlatform

**LearnPlatform** is an educational platform that allows the creation and management of courses, classes, resources, assignments, and a question-and-answer forum for interaction between students and teachers. The platform is built using **Spring Boot** for the backend and aims to provide a comprehensive environment for course administration and interaction among participants.

## Features

- **Course Management**: Admins can create courses, associate resources (such as learning tracks, bonuses, and forums), and define classes for these courses.
- **Classes and Offerings**: Allows the creation of classes with start and end dates. Each class is an offering of the course with possible content variations.
- **Assignment Management**: Teachers can create assignments for students, set acceptance criteria and grading. Students submit assignments for evaluation.
- **Question and Answer Forum**: Discussion topics related to course content with features for upvoting and marking the best answer.
- **Notifications**: Users receive notifications about new interactions, feedback on assignments, and changes to courses or classes.

## Technologies

This project was developed using the following technologies:

- **Spring Boot** - Main framework for backend development.
- **Spring Security** - For authentication and authorization.
- **Spring Data JPA** - For database communication.
- **H2 Database** - In-memory database (for development and testing).
- **Thymeleaf** - For rendering HTML pages (optional, depending on the front-end).
- **JUnit and Mockito** - For unit and integration testing.
- **Postman or CURL** - To test RESTful APIs.

## UML Diagram

[![modelo-conceitual-com-forum.png](https://i.postimg.cc/vBGFtzNx/modelo-conceitual-com-forum.png)](https://postimg.cc/xkp40KY9)

> **Note**: Replace the diagram path with the actual location of the file in your repository.

## How to Run the Project

### Prerequisites

- **Java 17+**: Make sure the JDK is installed.
- **Maven**: The project uses Maven for dependency management.

### Steps to Run

1. Clone this repository to your local machine:

   ```bash
   git clone https://github.com/your-username/learnplatform.git
   cd learnplatform
