package com.maurov.myplanner.controller;

import java.util.List;

import javax.validation.Valid;

import com.maurov.myplanner.entity.Task;
import com.maurov.myplanner.repository.TaskRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {

    private final String tasksEndpoint = "/api/v1/tasks";
    
    private TaskRepository taskRepository;

    @Autowired
    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    @GetMapping(tasksEndpoint)
    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    @PostMapping(tasksEndpoint)
    public Task postTask(@Valid @RequestBody Task task) {
        return this.taskRepository.save(task);
    }

    @DeleteMapping(tasksEndpoint + "/{id}")
    public void deleteTask(@PathVariable Long id) {
        this.taskRepository.deleteById(id);
    }

    @PutMapping(tasksEndpoint + "/{id}")
    public Task toggleTaskAsDone(
        @PathVariable Long id,
        @Valid @RequestBody Task task
    ) {
        Task toggledTask = this.taskRepository.getOne(id);
        toggledTask.setDone(!task.getDone());
        return this.taskRepository.save(toggledTask);
    }

}
