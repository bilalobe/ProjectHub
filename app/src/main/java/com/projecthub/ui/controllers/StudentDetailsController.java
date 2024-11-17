package com.projecthub.ui.controllers;

import com.projecthub.model.Student;
import com.projecthub.model.Submission;
import com.projecthub.service.SubmissionService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Controller for displaying Student details.
 */
@Component
public class StudentDetailsController {

    @Autowired
    private SubmissionService submissionService;

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
        List<Submission> submissions = submissionService.getSubmissionsByStudentId(student.getId());
        ObservableList<Submission> submissionList = FXCollections.observableArrayList(submissions);
        submissionTableView.setItems(submissionList);
    }
}