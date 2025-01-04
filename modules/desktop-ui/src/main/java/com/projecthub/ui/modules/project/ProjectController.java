package com.projecthub.ui.modules.project;

import com.projecthub.base.enums.ProjectStatus;
import com.projecthub.base.models.Project;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.time.LocalDate;

public class ProjectController {

    @FXML
    private Label projectNameLabel;

    @FXML
    private ProgressBar projectProgressBar;

    public void setProject(Project project) {
        projectNameLabel.setText(project.getName());
        projectProgressBar.setProgress(calculateProgress(project));
    }

    private double calculateProgress(Project project) {
        if (project.getStatus() == ProjectStatus.COMPLETED) {
            return 1.0;
        } else if (project.getStatus() == ProjectStatus.NOT_STARTED) {
            return 0.0;
        } else if (project.getStatus() == ProjectStatus.IN_PROGRESS) {
            // Calculate progress based on dates or tasks
            // For simplicity, let's assume a linear progress based on dates
            LocalDate startDate = project.getStartDate();
            LocalDate endDate = project.getEndDate();
            LocalDate now = LocalDate.now();

            if (startDate != null && endDate != null && !now.isBefore(startDate) && !now.isAfter(endDate)) {
                long totalDays = startDate.until(endDate).getDays();
                long elapsedDays = startDate.until(now).getDays();
                return (double) elapsedDays / totalDays;
            }
        }
        return 0.0;
    }
}
