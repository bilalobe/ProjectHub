package com.projecthub;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

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
                        Integer.parseInt(nextLine[0]),
                        Integer.parseInt(nextLine[1]),
                        Integer.parseInt(nextLine[2]),
                        nextLine[3],
                        nextLine[4] != null ? Integer.parseInt(nextLine[4]) : null
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
}