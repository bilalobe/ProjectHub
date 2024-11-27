package com.projecthub.ui.controllers.details;

import com.projecthub.dto.SubmissionSummary;
import com.projecthub.dto.ProjectSummary;
import com.projecthub.mapper.SubmissionMapper;
import com.projecthub.mapper.ProjectMapper;
import com.projecthub.model.Project;
import com.projecthub.model.Student;
import com.projecthub.model.Submission;
import com.projecthub.service.ProjectService;
import com.projecthub.service.SubmissionService;
import com.projecthub.repository.jpa.TeamRepository;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.model.Team;

/**
 * Controller for displaying Student details.
 */
@Controller
public class StudentDetailsController {

    private static final Logger logger = LoggerFactory.getLogger(StudentDetailsController.class);

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SubmissionMapper submissionMapper;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ProjectMapper projectMapper;

    @FXML
    private Label studentNameLabel;

    @FXML
    private TableView<Submission> submissionTableView;

    @FXML
    private TableColumn<Submission, Long> submissionIdColumn;

    @FXML
    private TableColumn<Submission, String> submissionProjectColumn;

    @FXML
    private TableColumn<Submission, String> submissionGradeColumn;

    private Student student;

    /**
     * Sets the Student to display.
     *
     * @param student the Student
     */
    public void setStudent(Student student) {
        this.student = student;
        updateUI();
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        submissionIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        submissionProjectColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProject().getName()));
        submissionGradeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getGrade() != null
                                ? cellData.getValue().getGrade().toString()
                                : "Not Graded"));
    }

    /**
     * Updates the UI elements with Student data.
     */
    private void updateUI() {
        if (student != null) {
            studentNameLabel.setText(student.getName());
            loadSubmissions();
        }
    }

    /**
     * Loads the submissions made by the Student.
     */
    private void loadSubmissions() {
        List<SubmissionSummary> submissionSummaries = submissionService.getSubmissionsByStudentId(student.getId());
        List<Submission> submissions = submissionSummaries.stream()
                .map(this::mapToSubmission)
                .filter(submission -> submission != null) // Filter out null submissions
                .collect(Collectors.toList());
        ObservableList<Submission> submissionList = FXCollections.observableArrayList(submissions);
        submissionTableView.setItems(submissionList);
    }

    /**
     * @param summary the SubmissionSummary
     * @return the Submission entity or null if the project is not found
     */
    private Submission mapToSubmission(SubmissionSummary summary) {
        Optional<ProjectSummary> projectSummaryOptional = projectService.getProjectById(summary.getProjectId());
        if (projectSummaryOptional.isPresent()) {
            ProjectSummary projectSummary = projectSummaryOptional.get();
            Team team = teamRepository.findById(projectSummary.getTeam())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + projectSummary.getTeam()));
            Project project = projectMapper.toProject(projectSummary, team);
            return submissionMapper.toSubmission(summary, project, student);
        } else {
            // Log a warning if the project is not found
            logger.warn("Project not found with ID: {}", summary.getProjectId());
            return null;
        }
    }
}