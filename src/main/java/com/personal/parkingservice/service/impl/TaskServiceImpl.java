package com.personal.parkingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import com.personal.parkingservice.entity.Task;
import com.personal.parkingservice.repository.TaskRepository;
import com.personal.parkingservice.service.TaskService;

import java.util.List;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task create(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    @Override
    public Task getById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
    }

    @Override
    public Task update(UUID id, Task updatedTask) {
        Task existing = getById(id);
        return taskRepository.save(updatedTask);
    }

    @Override
    public void delete(UUID id) {
        taskRepository.deleteById(id);
    }
}
