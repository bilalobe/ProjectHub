package com.projecthub.base.submission.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.submission.domain.value.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionExportService {
    private final SubmissionQueryService queryService;
    private final ObjectMapper objectMapper;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public byte[] exportToJson(List<Submission> submissions) throws com.fasterxml.jackson.core.JsonProcessingException {
        return objectMapper.writeValueAsBytes(submissions);
    }

    public byte[] exportToCsv(Iterable<Submission> submissions) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(baos);
             CSVWriter writer = new CSVWriter(osw)) {

            // Write header
            writer.writeNext(new String[] {
                "Submission ID", "Student ID", "Project ID", "Status",
                "Grade", "Is Late", "Submitted At", "Created At",
                "Last Modified At", "Comments Count"
            });

            // Write data
            for (Submission submission : submissions) {
                writer.writeNext(new String[] {
                    submission.getSubmissionId().toString(),
                    submission.getStudentId().toString(),
                    submission.getProjectId().toString(),
                    submission.getStatus().toString(),
                    submission.getGrade() != null ? submission.getGrade().toString() : "",
                    String.valueOf(submission.isLate()),
                    formatDate(submission.getSubmittedAt()),
                    formatDate(submission.getCreatedDate()),
                    formatDate(submission.getLastModifiedDate()),
                    String.valueOf(submission.getComments().size())
                });
            }

            writer.flush();
            return baos.toByteArray();
        }
    }

    public byte[] exportToExcel(Iterable<Submission> submissions) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Submissions");
            int rowNum = 0;

            // Create header row
            Row headerRow = sheet.createRow(rowNum);
            rowNum++;
            String[] headers = {
                "Submission ID", "Student ID", "Project ID", "Status",
                "Grade", "Is Late", "Submitted At", "Created At",
                "Last Modified At", "Comments Count", "Feedback"
            };

            CellStyle headerStyle = createHeaderStyle(workbook);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            CellStyle dateStyle = createDateStyle(workbook);
            for (Submission submission : submissions) {
                Row row = sheet.createRow(rowNum);
                rowNum++;
                int colNum = 0;

                row.createCell(colNum).setCellValue(submission.getSubmissionId().toString());
                colNum++;
                row.createCell(colNum).setCellValue(submission.getStudentId().toString());
                colNum++;
                row.createCell(colNum).setCellValue(submission.getProjectId().toString());
                colNum++;
                row.createCell(colNum).setCellValue(submission.getStatus().toString());
                colNum++;

                if (submission.getGrade() != null) {
                    row.createCell(colNum).setCellValue(submission.getGrade());
                    colNum++;
                } else {
                    row.createCell(colNum).setCellValue("");
                    colNum++;
                }

                row.createCell(colNum).setCellValue(submission.isLate());
                colNum++;

                Cell submittedAtCell = row.createCell(colNum);
                colNum++;
                if (submission.getSubmittedAt() != null) {
                    submittedAtCell.setCellValue(formatDate(submission.getSubmittedAt()));
                    submittedAtCell.setCellStyle(dateStyle);
                }

                Cell createdAtCell = row.createCell(colNum);
                colNum++;
                createdAtCell.setCellValue(formatDate(submission.getCreatedDate()));
                createdAtCell.setCellStyle(dateStyle);

                Cell modifiedAtCell = row.createCell(colNum);
                colNum++;
                modifiedAtCell.setCellValue(formatDate(submission.getLastModifiedDate()));
                modifiedAtCell.setCellStyle(dateStyle);

                row.createCell(colNum).setCellValue(submission.getComments().size());
                colNum++;
                row.createCell(colNum).setCellValue(submission.getFeedback());
            }

            // Autosize columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    public byte[] exportComments(UUID submissionId) throws IOException {
        Submission submission = queryService.findById(submissionId)
            .orElseThrow(() -> new IllegalArgumentException("Submission not found"));

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Comments");
            int rowNum = 0;

            // Create header row
            Row headerRow = sheet.createRow(rowNum);
            rowNum++;
            String[] headers = {"Comment ID", "Author ID", "Text", "Created At"};

            CellStyle headerStyle = createHeaderStyle(workbook);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            CellStyle dateStyle = createDateStyle(workbook);
            for (Comment comment : submission.getComments()) {
                Row row = sheet.createRow(rowNum);
                rowNum++;
                int colNum = 0;

                row.createCell(colNum).setCellValue(comment.getCommentId().toString());
                colNum++;
                row.createCell(colNum).setCellValue(comment.getAuthorId().toString());
                colNum++;
                row.createCell(colNum).setCellValue(comment.getText());
                colNum++;

                Cell createdAtCell = row.createCell(colNum);
                createdAtCell.setCellValue(formatDate(comment.getCreatedAt()));
                createdAtCell.setCellStyle(dateStyle);
            }

            // Autosize columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    private String formatDate(TemporalAccessor date) {
        return date != null ? DATE_FORMATTER.format(java.time.LocalDateTime.from(date)) : "";
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
        return style;
    }
}
