package com.projecthub.ui.controllers;

import com.projecthub.dto.TaskSummary;
import com.projecthub.service.TaskService;
import com.projecthub.ui.viewmodels.TaskViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    private TableColumn<TaskSummary, Long> taskIdColumn;

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
    public void initialize() {
        taskIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        taskNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        taskDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        taskStatusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        taskDueDateColumn.setCellValueFactory(cellData -> cellData.getValue().dueDateProperty());

        taskTableView.setItems(taskViewModel.getTasks());

        searchField.textProperty().bindBidirectional(taskViewModel.searchQueryProperty());
        searchField.textProperty().addListener((observable, oldValue, newValue) -> taskViewModel.searchTasks());
    }

    @FXML
    private void handleAddTask() {
        // Implement adding a new task
    }

    @FXML
    private void handleEditTask() {
        // Implement editing the selected task
    }

    @FXML
    private void handleDeleteTask() {
        TaskSummary selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskService.deleteTask(selectedTask.getId());
            taskViewModel.loadTasks();
        }
    }
}