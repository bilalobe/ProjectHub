package com.projecthub.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.projecthub.model.Project;
import com.projecthub.model.Student;
import com.projecthub.model.Submission;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVHandler {

    public static List<Submission> readSubmissionsFromCSV(String filePath) throws IOException {
        List<Submission> submissions = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // Create Submission objects from CSV data
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
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // Create Project objects from CSV data
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
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // Create Student objects from CSV data
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
            for (Student student : students) {
                String[] record = {
                        String.valueOf(student.getId()),
                        student.getName()
                };
                writer.writeNext(record);
            }
        }
    }
}