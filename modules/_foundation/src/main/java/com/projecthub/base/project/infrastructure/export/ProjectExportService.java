package com.projecthub.base.project.infrastructure.export;

import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.infrastructure.mapper.ProjectMapper;
import com.projecthub.base.project.infrastructure.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectExportService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public byte[] exportProjectsToExcel(UUID teamId) throws IOException {
        List<Project> projects = teamId != null ? 
            projectRepository.findByTeamId(teamId) : 
            projectRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Projects");
            createHeaderRow(sheet);
            populateProjectData(sheet, projects);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = createHeaderStyle(sheet.getWorkbook());
        
        String[] columns = {
            "Project ID", "Name", "Description", "Status", 
            "Start Date", "Deadline", "End Date", "Team",
            "Components", "Tasks", "Completion %"
        };

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        return headerStyle;
    }

    private void populateProjectData(Sheet sheet, List<Project> projects) {
        int rowNum = 1;
        for (Project project : projects) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(project.getId().toString());
            row.createCell(1).setCellValue(project.getName());
            row.createCell(2).setCellValue(project.getDescription());
            row.createCell(3).setCellValue(project.getStatus().name());
            
            if (project.getStartDate() != null) {
                row.createCell(4).setCellValue(project.getStartDate().format(DATE_FORMATTER));
            }
            
            if (project.getDeadline() != null) {
                row.createCell(5).setCellValue(project.getDeadline().format(DATE_FORMATTER));
            }
            
            if (project.getEndDate() != null) {
                row.createCell(6).setCellValue(project.getEndDate().format(DATE_FORMATTER));
            }
            
            row.createCell(7).setCellValue(project.getTeam().getName());
            row.createCell(8).setCellValue(project.getComponents().size());
            row.createCell(9).setCellValue(project.getTasks().size());
            row.createCell(10).setCellValue(calculateCompletionPercentage(project));
        }
    }

    private double calculateCompletionPercentage(Project project) {
        if (project.getTasks().isEmpty()) return 0.0;
        
        long completedTasks = project.getTasks().stream()
            .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
            .count();
            
        return (double) completedTasks / project.getTasks().size() * 100;
    }

    public ProjectExportDTO exportProjectDetails(UUID projectId) {
        Project project = projectRepository.findByIdWithDetails(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
            
        return new ProjectExportDTO(
            projectMapper.toDto(project),
            project.getComponents().stream().map(componentMapper::toDto).toList(),
            project.getTasks().stream().map(taskMapper::toDto).toList(),
            project.getMilestones().stream().map(milestoneMapper::toDto).toList(),
            calculateProjectMetrics(project)
        );
    }

    private Map<String, Object> calculateProjectMetrics(Project project) {
        return Map.of(
            "completionPercentage", calculateCompletionPercentage(project),
            "daysUntilDeadline", calculateDaysUntilDeadline(project),
            "taskBreakdown", calculateTaskBreakdown(project),
            "riskLevel", calculateRiskLevel(project)
        );
    }

    private long calculateDaysUntilDeadline(Project project) {
        if (project.getDeadline() == null) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), project.getDeadline());
    }

    private Map<TaskStatus, Long> calculateTaskBreakdown(Project project) {
        return project.getTasks().stream()
            .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));
    }

    private String calculateRiskLevel(Project project) {
        if (project.getStatus() == ProjectStatus.COMPLETED) return "NONE";
        if (isOverdue(project)) return "HIGH";
        if (isNearDeadline(project)) return "MEDIUM";
        return "LOW";
    }
}