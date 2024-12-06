package com.projecthub.ui.controllers.details;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.projecthub.dto.TaskSummary;
import com.projecthub.ui.viewmodels.TaskViewModel;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

@Component
public class TaskDetailsController {

    
    private TaskViewModel taskViewModel;

    @FXML
    private TableView<TaskSummary> taskTableView;

    @FXML
    private TableColumn<TaskSummary, Number> taskIdColumn;

    @FXML
    private TableColumn<TaskSummary, String> taskNameColumn;

    @FXML
    private TableColumn<TaskSummary, String> taskDescriptionColumn;

    @FXML
    private TableColumn<TaskSummary, String> taskStatusColumn;

    @FXML
    private TableColumn<TaskSummary, LocalDate> taskDueDateColumn;

    @FXML
    private TextField searchField;

    @FXML
    private VBox taskForm;

    @FXML
    private TextField taskNameField;

    @FXML
    private TextField taskDescriptionField;

    @FXML
    private TextField taskStatusField;

    @FXML
    private DatePicker taskDueDatePicker;

    @FXML
    private Button saveTaskButton;

    @FXML
    public void initialize() {
        taskIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        taskNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        taskDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        taskStatusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        taskDueDateColumn.setCellValueFactory(cellData -> cellData.getValue().dueDateProperty());

        taskTableView.setItems(taskViewModel.getTasks());

        searchField.textProperty().bindBidirectional(taskViewModel.searchQueryProperty());

        taskForm.setVisible(false);
    }

    @SuppressWarnings("unused")
    @FXML
    private void handleAddTask() {
        clearForm();
        taskForm.setVisible(true);
        saveTaskButton.setOnAction(event -> saveTask(null));
    }

    private void saveTask(Long taskId) {
        String name = taskNameField.getText();
        String description = taskDescriptionField.getText();
        String status = taskStatusField.getText();
        LocalDate dueDate = taskDueDatePicker.getValue();

        TaskSummary taskSummary = new TaskSummary(taskId, name, description, status, dueDate, null, null);
        taskViewModel.saveTask(taskSummary);
        taskForm.setVisible(false);
        taskViewModel.loadTasks();
    }

    private void clearForm() {
        taskNameField.clear();
        taskDescriptionField.clear();
        taskStatusField.clear();
        taskDueDatePicker.setValue(null);
    }

    @SuppressWarnings("unused")
    @FXML
    private void handleEditTask() {
        TaskSummary selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskNameField.setText(selectedTask.getName());
            taskDescriptionField.setText(selectedTask.getDescription());
            taskStatusField.setText(selectedTask.getStatus());
            taskDueDatePicker.setValue(selectedTask.getDueDate());
            taskForm.setVisible(true);
            saveTaskButton.setOnAction(event -> saveTask(selectedTask.getId()));
        }
    }

    @SuppressWarnings("unused")
    @FXML
    private void handleDeleteTask() {
        TaskSummary selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskViewModel.deleteTask(selectedTask.getId());
            taskViewModel.loadTasks();
        }
    }
}