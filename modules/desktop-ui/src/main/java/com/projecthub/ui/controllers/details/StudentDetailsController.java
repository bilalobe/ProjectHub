package com.projecthub.ui.controllers.details;

import com.projecthub.core.dto.SubmissionDTO;
import com.projecthub.core.models.Student;
import com.projecthub.core.models.Submission;
import com.projecthub.ui.controllers.BaseController;
import com.projecthub.ui.viewmodels.details.StudentDetailsViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

/**
 * Controller for displaying student details.
 */
@Controller
public class StudentDetailsController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(StudentDetailsController.class);

    private final StudentDetailsViewModel studentDetailsViewModel;

    @FXML
    private Label studentNameLabel;

    @FXML
    private TableView<Submission> submissionTableView;

    @FXML
    private TableColumn<Submission, UUID> submissionIdColumn;

    @FXML
    private TableColumn<Submission, String> submissionProjectColumn;

    @FXML
    private TableColumn<Submission, String> submissionGradeColumn;

    private Student student;

    /**
     * Constructor with dependencies injected.
     *
     * @param studentDetailsViewModel the StudentDetailsViewModel instance
     */
    public StudentDetailsController(StudentDetailsViewModel studentDetailsViewModel) {
        this.studentDetailsViewModel = studentDetailsViewModel;
    }

    /**
     * Sets the Student to display.
     *
     * @param student the Student
     */
    public void setStudent(Student student) {
        this.student = student;
        studentDetailsViewModel.setStudent(student);
        updateUI();
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        initializeTableColumns();
    }

    private void initializeTableColumns() {
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
            studentNameLabel.setText(student.getFirstName());
            loadSubmissions();
        }
    }

    /**
     * Loads the submissions made by the Student.
     */
    private void loadSubmissions() {
        try {
            List<SubmissionDTO> submissionSummaries = studentDetailsViewModel.getSubmissionsByStudentId(student.getId());
            List<Submission> submissions = submissionSummaries.stream()
                    .map(studentDetailsViewModel::mapToSubmission)
                    .toList();
            ObservableList<Submission> submissionList = FXCollections.observableArrayList(submissions);
            submissionTableView.setItems(submissionList);
        } catch (Exception e) {
            logger.error("Failed to load submissions", e);
            showAlert("Error", "Failed to load submissions.");
        }
    }

    /**
     * Shows an alert with the specified title and message.
     *
     * @param title   the title of the alert
     * @param message the message of the alert
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}