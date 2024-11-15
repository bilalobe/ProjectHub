package com.projecthub.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.exceptions.CsvValidationException;
import com.projecthub.model.Project;
import com.projecthub.model.Student;
import com.projecthub.model.Submission;
import com.projecthub.model.Team;
import com.projecthub.model.User;

public class CSVHandler {

    public static List<Submission> readSubmissionsFromCSV(String filePath) throws IOException {
        List<Submission> submissions = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            ColumnPositionMappingStrategy<Submission> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Submission.class);
            String[] memberFieldsToBindTo = {"id", "projectId", "studentId", "content", "grade"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                Submission submission = new Submission(
                        Long.valueOf(nextLine[0]),
                        Long.valueOf(nextLine[1]),
                        Long.valueOf(nextLine[2]),
                        nextLine[3],
                        nextLine[4] != null && !nextLine[4].isEmpty() ? Integer.parseInt(nextLine[4]) : null
                );
                submissions.add(submission);
            }
        } catch (CsvValidationException | IOException e) {
            throw new IOException("Error reading submissions from CSV", e);
        }
        return submissions;
    }

    public static void writeSubmissionsToCSV(List<Submission> submissions, String filePath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            ColumnPositionMappingStrategy<Submission> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Submission.class);
            String[] memberFieldsToBindTo = {"id", "projectId", "studentId", "content", "grade"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            for (Submission submission : submissions) {
                String[] record = {
                        String.valueOf(submission.getSubmissionId()),
                        String.valueOf(submission.getProjectId()),
                        String.valueOf(submission.getStudentId()),
                        submission.getFilePath(),
                        submission.getGrade() != null ? String.valueOf(submission.getGrade()) : ""
                };
                writer.writeNext(record);
            }
        }
    }

    public static List<Project> readProjectsFromCSV(String filePath) throws IOException {
        List<Project> projects = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            ColumnPositionMappingStrategy<Project> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Project.class);
            String[] memberFieldsToBindTo = {"id", "name", "description", "team"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                Project project = new Project(
                        Long.valueOf(nextLine[0]),
                        nextLine[1],
                        nextLine[2],
                        null // Assuming Team is not handled in CSV
                );
                projects.add(project);
            }
        } catch (CsvValidationException | IOException e) {
            throw new IOException("Error reading projects from CSV", e);
        }
        return projects;
    }

    public static void writeProjectsToCSV(List<Project> projects, String filePath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            ColumnPositionMappingStrategy<Project> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Project.class);
            String[] memberFieldsToBindTo = {"id", "name", "description", "team"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            for (Project project : projects) {
                String[] record = {
                        String.valueOf(project.getId()),
                        project.getName(),
                        project.getDescription()
                };
                writer.writeNext(record);
            }
        }
    }

    public static List<Student> readStudentsFromCSV(String filePath) throws IOException {
        List<Student> students = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            ColumnPositionMappingStrategy<Student> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Student.class);
            String[] memberFieldsToBindTo = {"id", "name"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                Student student = new Student(
                        Long.parseLong(nextLine[0]),
                        nextLine[1]
                );
                students.add(student);
            }
        } catch (CsvValidationException | IOException e) {
            throw new IOException("Error reading students from CSV", e);
        }
        return students;
    }

    public static void writeStudentsToCSV(List<Student> students, String filePath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            ColumnPositionMappingStrategy<Student> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Student.class);
            String[] memberFieldsToBindTo = {"id", "name"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            for (Student student : students) {
                String[] record = {
                        String.valueOf(student.getId()),
                        student.getName()
                };
                writer.writeNext(record);
            }
        }
    }

    public static List<Team> readTeamsFromCSV(String filePath) throws IOException {
        List<Team> teams = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            ColumnPositionMappingStrategy<Team> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Team.class);
            String[] memberFieldsToBindTo = {"id", "name", "school"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                Team team = new Team(
                        Long.valueOf(nextLine[0]),
                        nextLine[1],
                        null, // Assuming School is not handled in CSV
                        new ArrayList<>() // Assuming an empty list of projects
                );
                teams.add(team);
            }
        } catch (CsvValidationException | IOException e) {
            throw new IOException("Error reading teams from CSV", e);
        }
        return teams;
    }

    public static void writeTeamsToCSV(List<Team> teams, String filePath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            ColumnPositionMappingStrategy<Team> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Team.class);
            String[] memberFieldsToBindTo = {"id", "name", "school"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            for (Team team : teams) {
                String[] record = {
                        String.valueOf(team.getId()),
                        team.getName()
                };
                writer.writeNext(record);
            }
        }
    }

    public static List<User> readUsersFromCSV(String filePath) throws IOException {
        List<User> users = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            ColumnPositionMappingStrategy<User> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(User.class);
            String[] memberFieldsToBindTo = {"id", "username", "password", "teamId"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                User user = new User(
                        Long.valueOf(nextLine[0]),
                        nextLine[1],
                        nextLine[2],
                        new Team(Long.valueOf(nextLine[3]), null, null, new ArrayList<>())
                );
                users.add(user);
            }
        } catch (CsvValidationException | IOException e) {
            throw new IOException("Error reading users from CSV", e);
        }
        return users;
    }

    public static void writeUsersToCSV(List<User> users, String filePath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            ColumnPositionMappingStrategy<User> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(User.class);
            String[] memberFieldsToBindTo = {"id", "username", "password", "teamId"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            for (User user : users) {
                String[] record = {
                        String.valueOf(user.getId()),
                        user.getUsername(),
                        user.getPassword(),
                        String.valueOf(user.getTeam().getId())
                };
                writer.writeNext(record);
            }
        }
    }
}