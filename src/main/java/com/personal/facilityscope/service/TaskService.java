package com.personal.facilityscope.service;

import com.personal.facilityscope.entity.Task;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    Task create(Task task);
    List<Task> getAll();
    Task getById(UUID id);
    Task update(UUID id, Task updatedTask);
    void delete(UUID id);
}
