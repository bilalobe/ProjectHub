package com.projecthub.ui.viewmodels;

import com.projecthub.dto.TaskSummary;
import com.projecthub.service.TaskService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskViewModel {

    private final TaskService taskService;

    private final ObservableList<TaskSummary> tasks = FXCollections.observableArrayList();
    private final StringProperty searchQuery = new SimpleStringProperty();

    @Autowired
    public TaskViewModel(TaskService taskService) {
        this.taskService = taskService;
        initialize();
    }

    public ObservableList<TaskSummary> getTasks() {
        return tasks;
    }

    public StringProperty searchQueryProperty() {
        return searchQuery;
    }

    private void initialize() {
        loadTasks();
        searchQuery.addListener((observable, oldValue, newValue) -> searchTasks());
    }

    public void loadTasks() {
        List<TaskSummary> taskSummaries = taskService.getAllTasks().stream()
                .map(TaskSummary::new)
                .collect(Collectors.toList());
        tasks.setAll(taskSummaries);
    }

    public void searchTasks() {
        String query = searchQuery.get().toLowerCase();
        List<TaskSummary> filteredTasks = taskService.getAllTasks().stream()
                .map(TaskSummary::new)
                .filter(task -> task.getName().toLowerCase().contains(query) ||
                        task.getDescription().toLowerCase().contains(query))
                .collect(Collectors.toList());
        tasks.setAll(filteredTasks);
    }
}