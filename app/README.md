# ProjectHub

## Overview
ProjectHub is a Java-based project management application that utilizes CSV files for persistent storage. It allows users to manage projects, students, and submissions through a console-based interface.

## Features
- Manage projects with details such as ID, name, and deadline.
- Manage students with their respective IDs and names.
- Handle submissions associated with projects and students, including file paths and grades.
- Read and write project data using CSV files for easy data management.

## Project Structure
```
```
├── src
│   └── main
│       └── java
│           └── com
│               └── projecthub
│                   ├── Main.java
│                   ├── Project.java
│                   ├── Student.java
│                   ├── Submission.java
│                   └── CSVHandler.java
├── build.gradle
└── README.md
```

## Setup Instructions
1. Clone the repository:
   ```
   git clone <repository-url>
   cd ProjectHub
   ```

2. Build the project using Gradle:
   ```
   ./gradlew build
   ```

3. Run the application:
   ```
   ./gradlew run
   ```

## Usage
Upon running the application, users will be presented with a console-based menu to interact with the project management features. Follow the prompts to manage projects, students, and submissions.

## Dependencies
- OpenCSV for handling CSV data.
- JUnit for testing.

## License
This project is licensed under the MIT License. See the LICENSE file for more details.