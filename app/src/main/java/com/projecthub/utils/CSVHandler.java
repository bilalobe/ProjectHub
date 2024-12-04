// package com.projecthub.utils;

// import com.opencsv.CSVReader;
// import com.opencsv.CSVWriter;
// import com.opencsv.bean.*;
// import com.opencsv.exceptions.CsvDataTypeMismatchException;
// import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
// import com.projecthub.dto.*;
// import com.projecthub.exception.ResourceNotFoundException;
// import com.projecthub.mapper.*;
// import com.projecthub.model.*;
// import com.projecthub.service.*;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;

// import java.io.FileReader;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.util.*;

// @Component
// public class CsvHandler {

//     @Autowired
//     private ProjectService projectService;

//     @Autowired
//     private StudentService studentService;

//     @Autowired
//     private TeamService teamService;

//     @Autowired
//     private SchoolService schoolService;

//     @Autowired
//     private SubmissionService submissionService;

//     /**
//      * Reads submissions from a CSV file.
//      *
//      * @param filePath the path to the CSV file
//      * @return a list of SubmissionSummary DTOs
//      * @throws IOException if an I/O error occurs
//      */
//     public List<SubmissionSummary> readSubmissionsFromCSV(String filePath) throws IOException {
//         List<SubmissionSummary> submissions = new ArrayList<>();
//         try (CSVReader reader = new CSVReader(new FileReader(filePath))) {

//             ColumnPositionMappingStrategy<SubmissionCSV> strategy = new ColumnPositionMappingStrategy<>();
//             strategy.setType(SubmissionCSV.class);
//             String[] memberFieldsToBindTo = {"id", "projectId", "studentId", "content", "grade"};
//             strategy.setColumnMapping(memberFieldsToBindTo);

//             List<SubmissionCSV> csvSubmissions = new CsvToBeanBuilder<SubmissionCSV>(reader)
//                     .withMappingStrategy(strategy)
//                     .withSkipLines(1) // Skip header if present
//                     .build()
//                     .parse();

//             for (SubmissionCSV csvSubmission : csvSubmissions) {
//                 // Retrieve ProjectSummary
//                 ProjectSummary projectSummary = projectService.getProjectById(csvSubmission.getProjectId())
//                         .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + csvSubmission.getProjectId()));

//                 // Retrieve StudentSummary
//                 StudentSummary studentSummary = studentService.getStudentById(csvSubmission.getStudentId())
//                         .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + csvSubmission.getStudentId()));

//                 // Create SubmissionSummary
//                 SubmissionSummary submissionSummary = new SubmissionSummary(
//                         csvSubmission.getId(),
//                         projectSummary.getId(),
//                         studentSummary.getId(),
//                         csvSubmission.getContent(),
//                         csvSubmission.getGrade()
//                 );

//                 submissions.add(submissionSummary);
//             }
//         } catch (IOException e) {
//             throw new IOException("Error reading submissions from CSV: " + e.getMessage(), e);
//         }
//         return submissions;
//     }

//     /**
//      * Writes submissions to a CSV file.
//      *
//      * @param submissions the list of SubmissionSummary DTOs
//      * @param filePath    the path to the CSV file
//      * @throws IOException if an I/O error occurs
//      */
//     public void writeSubmissionsToCSV(List<SubmissionSummary> submissions, String filePath) throws IOException {
//         try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {

//             ColumnPositionMappingStrategy<SubmissionCSV> strategy = new ColumnPositionMappingStrategy<>();
//             strategy.setType(SubmissionCSV.class);
//             String[] memberFieldsToBindTo = {"id", "projectId", "studentId", "content", "grade"};
//             strategy.setColumnMapping(memberFieldsToBindTo);

//             List<SubmissionCSV> csvSubmissions = new ArrayList<>();
//             for (SubmissionSummary submission : submissions) {
//                 SubmissionCSV csvSubmission = new SubmissionCSV(
//                         submission.getId(),
//                         submission.getProjectId(),
//                         submission.getStudentId(),
//                         submission.getContent(),
//                         submission.getGrade()
//                 );
//                 csvSubmissions.add(csvSubmission);
//             }

//             StatefulBeanToCsv<SubmissionCSV> beanToCsv = new StatefulBeanToCsvBuilder<SubmissionCSV>(writer)
//                     .withMappingStrategy(strategy)
//                     .build();

//             beanToCsv.write(csvSubmissions);
//         } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
//             throw new IOException("Error writing submissions to CSV: " + e.getMessage(), e);
//         }
//     }

//     // Similar methods for Projects, Students, Teams, and Users...

//     // Helper inner classes to represent CSV mappings
//     /**
//      * Inner class to represent a Submission record in CSV.
//      */
//     private static class SubmissionCSV {
//         private UUID id;
//         private UUID projectId;
//         private UUID studentId;
//         private String content;
//         private Integer grade;

//         // Constructors, getters, and setters

//         public SubmissionCSV() {}

//         public SubmissionCSV(UUID id, UUID projectId, UUID studentId, String content, Integer grade) {
//             this.id = id;
//             this.projectId = projectId;
//             this.studentId = studentId;
//             this.content = content;
//             this.grade = grade;
//         }

//         // Getters and setters...

//         public UUID getId() { return id; }
//         public void setId(UUID id) { this.id = id; }
//         public UUID getProjectId() { return projectId; }
//         public void setProjectId(UUID projectId) { this.projectId = projectId; }
//         public UUID getStudentId() { return studentId; }
//         public void setStudentId(UUID studentId) { this.studentId = studentId; }
//         public String getContent() { return content; }
//         public void setContent(String content) { this.content = content; }
//         public Integer getGrade() { return grade; }
//         public void setGrade(Integer grade) { this.grade = grade; }
//     }

//     // Similarly, create inner classes for ProjectCSV, StudentCSV, TeamCSV, AppUserCSV

//     // For example:

//     /**
//      * Inner class to represent a Project record in CSV.
//      */
//     private static class ProjectCSV {
//         private UUID id;
//         private String name;
//         private String description;
//         private UUID teamId;

//         // Constructors, getters, and setters

//         public ProjectCSV() {}

//         public ProjectCSV(UUID id, String name, String description, UUID teamId) {
//             this.id = id;
//             this.name = name;
//             this.description = description;
//             this.teamId = teamId;
//         }

//         // Getters and setters...

//         public UUID getId() { return id; }
//         public void setId(UUID id) { this.id = id; }
//         public String getName() { return name; }
//         public void setName(String name) { this.name = name; }
//         public String getDescription() { return description; }
//         public void setDescription(String description) { this.description = description; }
//         public UUID getTeamId() { return teamId; }
//         public void setTeamId(UUID teamId) { this.teamId = teamId; }
//     }

//     // And similar for StudentCSV, TeamCSV, AppUserCSV

//     // Provide read and write methods for projects:

//     /**
//      * Reads projects from a CSV file.
//      *
//      * @param filePath the path to the CSV file
//      * @return a list of ProjectSummary DTOs
//      * @throws IOException if an I/O error occurs
//      */
//     public List<ProjectSummary> readProjectsFromCSV(String filePath) throws IOException {
//         List<ProjectSummary> projects = new ArrayList<>();
//         try (CSVReader reader = new CSVReader(new FileReader(filePath))) {

//             ColumnPositionMappingStrategy<ProjectCSV> strategy = new ColumnPositionMappingStrategy<>();
//             strategy.setType(ProjectCSV.class);
//             String[] memberFieldsToBindTo = {"id", "name", "description", "teamId"};
//             strategy.setColumnMapping(memberFieldsToBindTo);

//             List<ProjectCSV> csvProjects = new CsvToBeanBuilder<ProjectCSV>(reader)
//                     .withMappingStrategy(strategy)
//                     .withSkipLines(1)
//                     .build()
//                     .parse();

//             for (ProjectCSV csvProject : csvProjects) {
//                 // Retrieve TeamSummary
//                 TeamSummary teamSummary = teamService.getTeamById(csvProject.getTeamId())
//                         .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + csvProject.getTeamId()));

//                 // Create ProjectSummary
//                 ProjectSummary projectSummary = new ProjectSummary(
//                         csvProject.getId(),
//                         csvProject.getName(),
//                         csvProject.getDescription(),
//                         teamSummary.getId()
//                 );

//                 projects.add(projectSummary);
//             }
//         } catch (IOException e) {
//             throw new IOException("Error reading projects from CSV: " + e.getMessage(), e);
//         }
//         return projects;
//     }

//     /**
//      * Writes projects to a CSV file.
//      *
//      * @param projects the list of ProjectSummary DTOs to write
//      * @param filePath the path to the CSV file
//      * @throws IOException if an I/O error occurs
//      */
//     public void writeProjectsToCSV(List<ProjectSummary> projects, String filePath) throws IOException {
//         try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
    
//             ColumnPositionMappingStrategy<ProjectCSV> strategy = new ColumnPositionMappingStrategy<>();
//             strategy.setType(ProjectCSV.class);
//             String[] memberFieldsToBindTo = {"id", "name", "description", "teamId"};
//             strategy.setColumnMapping(memberFieldsToBindTo);
    
//             List<ProjectCSV> csvProjects = new ArrayList<>();
//             for (ProjectSummary project : projects) {
//                 ProjectCSV csvProject = new ProjectCSV(
//                         project.getId(),
//                         project.getName(),
//                         project.getDescription(),
//                         project.getTeamId()
//                 );
//                 csvProjects.add(csvProject);
//             }
    
//             StatefulBeanToCsv<ProjectCSV> beanToCsv = new StatefulBeanToCsvBuilder<ProjectCSV>(writer)
//                     .withMappingStrategy(strategy)
//                     .build();
    
//             beanToCsv.write(csvProjects);
//         } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
//             throw new IOException("Error writing projects to CSV: " + e.getMessage(), e);
//         }
//     }

//     /**
//      * Reads students from a CSV file.
//      *
//      * @param filePath the path to the CSV file
//      * @return a list of StudentSummary DTOs
//      * @throws IOException if an I/O error occurs
//      */
//     public List<StudentSummary> readStudentsFromCSV(String filePath) throws IOException {
//         List<StudentSummary> students = new ArrayList<>();
//         try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
    
//             ColumnPositionMappingStrategy<StudentCSV> strategy = new ColumnPositionMappingStrategy<>();
//             strategy.setType(StudentCSV.class);
//             String[] memberFieldsToBindTo = {"id", "name", "teamId"};
//             strategy.setColumnMapping(memberFieldsToBindTo);
    
//             List<StudentCSV> csvStudents = new CsvToBeanBuilder<StudentCSV>(reader)
//                     .withMappingStrategy(strategy)
//                     .withSkipLines(1) // Skip header if present
//                     .build()
//                     .parse();
    
//             for (StudentCSV csvStudent : csvStudents) {
//                 // Create StudentSummary
//                 StudentSummary studentSummary = new StudentSummary(
//                         csvStudent.getId(),
//                         csvStudent.getName(),
//                         csvStudent.getTeamId()
//                 );
    
//                 students.add(studentSummary);
//             }
//         } catch (IOException e) {
//             throw new IOException("Error reading students from CSV: " + e.getMessage(), e);
//         }
//         return students;
//     }

//     /**
//      * Writes students to a CSV file.
//      *
//      * @param students the list of StudentSummary DTOs to write
//      * @param filePath the path to the CSV file
//      * @throws IOException if an I/O error occurs
//      */
//     public void writeStudentsToCSV(List<StudentSummary> students, String filePath) throws IOException {
//         try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
    
//             ColumnPositionMappingStrategy<StudentCSV> strategy = new ColumnPositionMappingStrategy<>();
//             strategy.setType(StudentCSV.class);
//             String[] memberFieldsToBindTo = {"id", "name", "teamId"};
//             strategy.setColumnMapping(memberFieldsToBindTo);
    
//             List<StudentCSV> csvStudents = new ArrayList<>();
//             for (StudentSummary student : students) {
//                 StudentCSV csvStudent = new StudentCSV(
//                         student.getId(),
//                         student.getName(),
//                         student.getTeamId()
//                 );
//                 csvStudents.add(csvStudent);
//             }
    
//             StatefulBeanToCsv<StudentCSV> beanToCsv = new StatefulBeanToCsvBuilder<StudentCSV>(writer)
//                     .withMappingStrategy(strategy)
//                     .build();
    
//             beanToCsv.write(csvStudents);
//         } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
//             throw new IOException("Error writing students to CSV: " + e.getMessage(), e);
//         }
//     }

//     /**
//      * Reads teams from a CSV file.
//      *
//      * @param filePath the path to the CSV file
//      * @return a list of TeamSummary DTOs
//      * @throws IOException if an I/O error occurs
//      */
//     public List<TeamSummary> readTeamsFromCSV(String filePath) throws IOException {
//         List<TeamSummary> teams = new ArrayList<>();
//         try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
    
//             ColumnPositionMappingStrategy<TeamCSV> strategy = new ColumnPositionMappingStrategy<>();
//             strategy.setType(TeamCSV.class);
//             String[] memberFieldsToBindTo = {"id", "name", "schoolId", "cohortId"};
//             strategy.setColumnMapping(memberFieldsToBindTo);
    
//             List<TeamCSV> csvTeams = new CsvToBeanBuilder<TeamCSV>(reader)
//                     .withMappingStrategy(strategy)
//                     .withSkipLines(1) // Skip header if present
//                     .build()
//                     .parse();
    
//             for (TeamCSV csvTeam : csvTeams) {
//                 // Create TeamSummary
//                 TeamSummary teamSummary = new TeamSummary(
//                         csvTeam.getId(),
//                         csvTeam.getName(),
//                         csvTeam.getSchoolId(),
//                         csvTeam.getCohortId()
//                 );
    
//                 teams.add(teamSummary);
//             }
//         } catch (IOException e) {
//             throw new IOException("Error reading teams from CSV: " + e.getMessage(), e);
//         }
//         return teams;
//     }
    
//     /**
//      * Writes teams to a CSV file.
//      *
//      * @param teams    the list of TeamSummary DTOs to write
//      * @param filePath the path to the CSV file
//      * @throws IOException if an I/O error occurs
//      */
//     public void writeTeamsToCSV(List<TeamSummary> teams, String filePath) throws IOException {
//         try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
    
//             ColumnPositionMappingStrategy<TeamCSV> strategy = new ColumnPositionMappingStrategy<>();
//             strategy.setType(TeamCSV.class);
//             String[] memberFieldsToBindTo = {"id", "name", "schoolId", "cohortId"};
//             strategy.setColumnMapping(memberFieldsToBindTo);
    
//             List<TeamCSV> csvTeams = new ArrayList<>();
//             for (TeamSummary team : teams) {
//                 TeamCSV csvTeam = new TeamCSV(
//                         team.getId(),
//                         team.getName(),
//                         team.getSchoolId(),
//                         team.getCohortId()
//                 );
//                 csvTeams.add(csvTeam);
//             }
    
//             StatefulBeanToCsv<TeamCSV> beanToCsv = new StatefulBeanToCsvBuilder<TeamCSV>(writer)
//                     .withMappingStrategy(strategy)
//                     .build();
    
//             beanToCsv.write(csvTeams);
//         } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
//             throw new IOException("Error writing teams to CSV: " + e.getMessage(), e);
//         }
//     }

//         /**
//      * Reads app users from a CSV file.
//      *
//      * @param filePath the path to the CSV file
//      * @return a list of AppUserSummary DTOs
//      * @throws IOException if an I/O error occurs
//      */
//     public List<AppUserSummary> readUsersFromCSV(String filePath) throws IOException {
//         List<AppUserSummary> users = new ArrayList<>();
//         try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
    
//             ColumnPositionMappingStrategy<AppUserCSV> strategy = new ColumnPositionMappingStrategy<>();
//             strategy.setType(AppUserCSV.class);
//             String[] memberFieldsToBindTo = {"id", "username", "teamId"};
//             strategy.setColumnMapping(memberFieldsToBindTo);
    
//             List<AppUserCSV> csvUsers = new CsvToBeanBuilder<AppUserCSV>(reader)
//                     .withMappingStrategy(strategy)
//                     .withSkipLines(1) // Skip header if present
//                     .build()
//                     .parse();
    
//             for (AppUserCSV csvUser : csvUsers) {
//                 // Create AppUserSummary
//                 AppUserSummary userSummary = new AppUserSummary(
//                         csvUser.getId(),
//                         csvUser.getUsername(),
//                         csvUser.getTeamId()
//                 );
    
//                 users.add(userSummary);
//             }
//         } catch (IOException e) {
//             throw new IOException("Error reading app users from CSV: " + e.getMessage(), e);
//         }
//         return users;
//     }
// }