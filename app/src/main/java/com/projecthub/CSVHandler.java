package com.projecthub;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

public class CSVHandler {

    public static List<Project> readProjectsFromCSV(String filePath) throws IOException {
        List<Project> projects = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // Create Project objects from CSV data
                Project project = new Project(Integer.parseInt(nextLine[0]), nextLine[1], nextLine[2], nextLine[3]);
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
                String[] record = {String.valueOf(project.getId()), project.getName(), project.getDeadline(), project.getDescription()};
                writer.writeNext(record);
            }
        }
    }

    // Similar methods for reading/writing Students and Submissions
}