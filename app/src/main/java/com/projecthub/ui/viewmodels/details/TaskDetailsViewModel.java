package com.projecthub.ui.viewmodels.details;

import com.projecthub.dto.TaskDTO;
import com.projecthub.service.TaskService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ViewModel for managing task details.
 */
@Component
public class TaskDetailsViewModel {

    private static final Logger logger = LoggerFactory.getLogger(TaskDetailsViewModel.class);

    private final TaskService taskService;

    private final ObservableList<TaskDTO> tasks = FXCollections.observableArrayList();
    private final SimpleObjectProperty<TaskDTO> selectedTask = new SimpleObjectProperty<>();
    private final SimpleStringProperty searchQuery = new SimpleStringProperty();

    /**
     * Constructor with dependency injection.
     *
     * @param taskService the task service
     */
    public TaskDetailsViewModel(TaskService taskService) {
        this.taskService = taskService;
        initialize();
    }

    public ObservableList<TaskDTO> getTasks() {
        return tasks;
    }

    public SimpleObjectProperty<TaskDTO> selectedTaskProperty() {
        return selectedTask;
    }

    public SimpleStringProperty searchQueryProperty() {
        return searchQuery;
    }

    public TaskDTO getSelectedTask() {
        return selectedTask.get();
    }

    private void initialize() {
        loadTasks();
        searchQuery.addListener((observable, oldValue, newValue) -> onSearchQueryChanged(observable, oldValue, newValue));
    }

    /**
     * Loads tasks from the service.
     */
    public void loadTasks() {
        try {
            List<TaskDTO> taskSummaries = taskService.getAllTasks();
            tasks.setAll(taskSummaries);
        } catch (Exception e) {
            logger.error("Failed to load tasks", e);
        }
    }

    /**
     * Searches tasks based on the search query.
     */
    public void searchTasks() {
        String query = searchQuery.get();
        if (query == null) {
            logger.warn("Search query is null in searchTasks()");
            return;
        }
        try {
            String lowerCaseQuery = query.toLowerCase();
            List<TaskDTO> filteredTasks = taskService.getAllTasks().stream()
                    .filter(task -> task.getName().toLowerCase().contains(lowerCaseQuery) ||
                            task.getDescription().toLowerCase().contains(lowerCaseQuery))
                    .collect(Collectors.toList());
            tasks.setAll(filteredTasks);
        } catch (Exception e) {
            logger.error("Failed to search tasks with query: {}", query, e);
        }
    }

    /**
     * Saves a task.
     *
     * @param taskSummary the task DTO
     */
    public void saveTask(TaskDTO taskSummary) {
        if (taskSummary == null) {
            logger.warn("TaskSummary is null in saveTask()");
            return;
        }
        try {
            taskService.saveTask(taskSummary);
            loadTasks();
        } catch (Exception e) {
            logger.error("Failed to save task: {}", taskSummary, e);
        }
    }

    /**
     * Deletes a task by ID.
     *
     * @param taskId the task ID
     */
    public void deleteTask(UUID taskId) {
        if (taskId == null) {
            logger.warn("TaskId is null in deleteTask()");
            return;
        }
        try {
            taskService.deleteTask(taskId);
            loadTasks();
        } catch (Exception e) {
            logger.error("Failed to delete task ID: {}", taskId, e);
        }
    }

    /**
     * Selects a task.
     *
     * @param task the task DTO
     */
    public void selectTask(TaskDTO task) {
        selectedTask.set(task);
    }

    /**
     * Handles changes to the search query.
     *
     * @param observable the observable value
     * @param oldValue   the old search query
     * @param newValue   the new search query
     */
    private void onSearchQueryChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        searchTasks();
    }
}