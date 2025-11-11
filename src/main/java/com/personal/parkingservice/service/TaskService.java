package com.personal.parkingservice.service;

import java.util.List;
import java.util.UUID;

import com.personal.parkingservice.entity.Task;

public interface TaskService {
    Task create(Task task);

    List<Task> getAll();

    Task getById(UUID id);

    Task update(UUID id, Task updatedTask);

    void delete(UUID id);
}
