package com.projecthub.ui.modules.task;

import com.projecthub.base.shared.utils.UUIDStringConverter;
import com.projecthub.base.task.api.dto.TaskDTO;
import com.projecthub.ui.shared.utils.BaseController;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Controller for managing task details.
 */
@Component
public class TaskDetailsController extends BaseController {

    private final TaskDetailsViewModel taskViewModel;

    @FXML
    private TableView<TaskDTO> taskTableView;

    @FXML
    private TableColumn<TaskDTO, String> taskIdColumn;

    @FXML
    private TableColumn<TaskDTO, String> taskNameColumn;

    @FXML
    private TableColumn<TaskDTO, String> taskDescriptionColumn;

    @FXML
    private TableColumn<TaskDTO, String> taskStatusColumn;

    @FXML
    private TableColumn<TaskDTO, LocalDate> taskDueDateColumn;

    @FXML
    private TextField searchField;

    @FXML
    private VBox taskForm;

    @FXML
    private TextField taskNameField;

    @FXML
    private TextArea taskDescriptionField;

    @FXML
    private ComboBox<String> taskStatusField;

    @FXML
    private DatePicker taskDueDatePicker;

    @FXML
    private Button saveTaskButton;

    @FXML
    private Button deleteTaskButton;

    @FXML
    private Button clearButton;

    /**
     * Constructor with dependencies injected.
     *
     * @param taskViewModel the TaskDetailsViewModel instance
     */
    public TaskDetailsController(TaskDetailsViewModel taskViewModel) {
        this.taskViewModel = taskViewModel;
    }

    @FXML
    public void initialize() {
        new UUIDStringConverter();

        taskIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        taskNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        taskDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        taskStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        taskDueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        taskTableView.setItems(taskViewModel.getTasks());

        searchField.textProperty().bindBidirectional(taskViewModel.searchQueryProperty());

        taskForm.setVisible(false);

        saveTaskButton.setOnAction(this::handleSaveTask);
        deleteTaskButton.setOnAction(this::handleDeleteTask);

        // Disable clear button when all fields are empty
        clearButton.disableProperty().bind(
            taskNameField.textProperty().isEmpty()
                .and(taskDescriptionField.textProperty().isEmpty())
                .and(taskStatusField.valueProperty().isNull())
                .and(taskDueDatePicker.valueProperty().isNull())
        );
    }

    @FXML
    private void handleAddTask(ActionEvent event) {
        clearForm();
        taskForm.setVisible(true);
        saveTaskButton.setOnAction(this::handleSaveTask);
    }

    @FXML
    private void handleSaveTask(ActionEvent event) {
        saveTask(UUID.randomUUID());
    }

    @FXML
    private void handleSaveTask(ActionEvent event, UUID taskId) {
        saveTask(taskId);
    }

    private void saveTask(UUID taskId) {
        try {
            String name = taskNameField.getText();
            String description = taskDescriptionField.getText();
            String status = taskStatusField.getValue();
            LocalDate dueDate = taskDueDatePicker.getValue();

            if (!isValidTaskInput(name, description)) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please provide valid task details.");
                return;
            }

            Task<Void> saveTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        TaskDTO taskSummary = new TaskDTO(taskId, name, description, status, dueDate, null, null, null, null, false, null);
                        taskViewModel.saveTask(taskSummary);
                    } catch (Exception e) {
                        logger.error("Error saving task", e);
                        throw e;
                    }
                    return null;
                }
            };

            saveTask.setOnSucceeded(this::handleSaveTaskSuccess);
            saveTask.setOnFailed(this::handleSaveTaskFailure);

            new Thread(saveTask).start();
        } catch (Exception e) {
            showAlert("Error", "Failed to save task: " + e.getMessage());
        }
    }

    private void handleSaveTaskSuccess(WorkerStateEvent event) {
        showAlert("Success", "Task saved successfully.");
        taskForm.setVisible(false);
        taskViewModel.loadTasks();
    }

    private void handleSaveTaskFailure(WorkerStateEvent event) {
        Throwable exception = event.getSource().getException();
        showAlert("Error", "Failed to save task: " + exception.getMessage());
    }

    @FXML
    private void handleEditTask(ActionEvent event) {
        TaskDTO selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskNameField.setText(selectedTask.getName());
            taskDescriptionField.setText(selectedTask.getDescription());
            taskStatusField.setValue(selectedTask.getStatus());
            taskDueDatePicker.setValue(selectedTask.getDueDate());
            taskForm.setVisible(true);
            saveTaskButton.setOnAction(e -> handleSaveTask(e, selectedTask.getId()));
        }
    }

    @FXML
    private void handleDeleteTask(ActionEvent event) {
        TaskDTO selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskViewModel.deleteTask(selectedTask.getId());
            taskViewModel.loadTasks();
            showAlert("Success", "Task deleted successfully.");
        }
    }

    private void clearForm() {
        taskNameField.clear();
        taskDescriptionField.clear();
        taskStatusField.setValue(null);
        taskDueDatePicker.setValue(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isValidTaskInput(String name, String description) {
        return name != null && !name.trim().isEmpty();
    }
}
