// package com.projecthub.repository.jpa;

// import com.projecthub.model.Task;
// import com.projecthub.repository.TaskRepository;
// import org.springframework.context.annotation.Profile;
// import org.springframework.stereotype.Repository;

// import java.util.List;

// @Repository
// @Profile("jpa")
// public class TaskJpaRepositoryImpl implements TaskRepository {

//     private final TaskJpaRepository taskJpaRepository;

//     public TaskJpaRepositoryImpl(TaskJpaRepository taskJpaRepository) {
//         this.taskJpaRepository = taskJpaRepository;
//     }

//     @Override
//     public List<Task> findAll() {
//         return taskJpaRepository.findAll();
//     }

//     @Override
//     public List<Task> findByProjectId(Long projectId) {
//         return taskJpaRepository.findByProjectId(projectId);
//     }

//     @Override
//     public List<Task> findByAssignedUserId(Long userId) {
//         return taskJpaRepository.findByAssignedUserId(userId);
//     }

//     @Override
//     public Optional<Task> findById(Long id) {
//         return taskJpaRepository.findById(id);
//     }

//     @Override
//     public void delete(Task task) {
//         taskJpaRepository.delete(task);
//     }

//     @Override
//     public Task save(Task task) {
//         return taskJpaRepository.save(task);
//     }
// }