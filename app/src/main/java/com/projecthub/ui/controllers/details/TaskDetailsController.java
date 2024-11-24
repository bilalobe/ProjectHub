package com.projecthub.ui.controllers;

import com.projecthub.dto.TaskSummary;
import com.projecthub.service.TaskService;
import com.projecthub.ui.viewmodels.TaskViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskDetailsController {

    @Autowired
    private TaskService taskService;

    @Autowired
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
    private TableColumn<TaskSummary, String> taskDueDateColumn;

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
        taskIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        taskNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        taskDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        taskStatusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        taskDueDateColumn.setCellValueFactory(cellData -> cellData.getValue().dueDateProperty());

        taskTableView.setItems(taskViewModel.getTasks());

        searchField.textProperty().bindBidirectional(taskViewModel.searchQueryProperty());
        searchField.textProperty().addListener((observable, oldValue, newValue) -> taskViewModel.searchTasks());

        taskForm.setVisible(false);
    }

    @FXML
    private void handleAddTask() {
        clearForm();
        taskForm.setVisible(true);
        saveTaskButton.setOnAction(event -> saveTask(null));
    }

    @FXML
    private void handleEditTask() {
        TaskSummary selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            populateForm(selectedTask);
            taskForm.setVisible(true);
            saveTaskButton.setOnAction(event -> saveTask(selectedTask.getId()));
        }
    }

    @FXML
    private void handleDeleteTask() {
        TaskSummary selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskService.deleteTask(selectedTask.getId());
            taskViewModel.loadTasks();
        }
    }

    private void saveTask(Long taskId) {
        String name = taskNameField.getText();
        String description = taskDescriptionField.getText();
        String status = taskStatusField.getText();
        String dueDate = taskDueDatePicker.getValue() != null ? taskDueDatePicker.getValue().toString() : null;

        TaskSummary taskSummary = new TaskSummary(taskId, name, description, status, dueDate);
        if (taskId == null) {
            taskService.saveTask(taskSummary);
        } else {
            taskService.updateTask(taskId, taskSummary);
        }
        taskViewModel.loadTasks();
        taskForm.setVisible(false);
    }

    private void clearForm() {
        taskNameField.clear();
        taskDescriptionField.clear();
        taskStatusField.clear();
        taskDueDatePicker.setValue(null);
    }

    private void populateForm(TaskSummary taskSummary) {
        taskNameField.setText(taskSummary.getName());
        taskDescriptionField.setText(taskSummary.getDescription());
        taskStatusField.setText(taskSummary.getStatus());
        taskDueDatePicker.setValue(taskSummary.getDueDate() != null ? java.time.LocalDate.parse(taskSummary.getDueDate()) : null);
    }
}