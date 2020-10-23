package com.maurov.myplanner.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maurov.myplanner.dto.TaskDTO;
import com.maurov.myplanner.entity.Task;
import com.maurov.myplanner.repository.TaskRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class TaskController {

    private static final String TASKS_ENDPOINT = "/api/v1/tasks";
    private static final ModelMapper MODEL_MAPPER = new ModelMapper();
    
    private TaskRepository taskRepository;

    @Autowired
    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    @GetMapping(TASKS_ENDPOINT)
    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    @PostMapping(TASKS_ENDPOINT)
    public Task postTask(@Valid @RequestBody TaskDTO taskObject) {
        Task task = MODEL_MAPPER.map(taskObject, Task.class);
        return this.taskRepository.save(task);
    }

    @DeleteMapping(TASKS_ENDPOINT + "/{id}")
    public void deleteTask(@PathVariable Long id) {
        this.taskRepository.deleteById(id);
    }

    @PutMapping(TASKS_ENDPOINT + "/{id}")
    public Task editTask(
        @PathVariable Long id,
        @RequestParam("action") Optional<String> action,
        @Valid @RequestBody TaskDTO taskObject
    ) {
        Task task = MODEL_MAPPER.map(taskObject, Task.class);
        Task editedTask = this.taskRepository.findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "¡Tarea no encontrada!"
                )
            );

        if (action.isPresent()) {
            if ("toggleAsDone".equals(action.get())) {
                editedTask.setDone(!task.getDone());
            } else if ("edit".equals(action.get())) {
                editedTask = task;
            } else {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe indicar una acción valida a realizar con la tarea, " +
                    "en la URL ('toggleAsDone' o 'edit')"
                );
            }
        }

        return this.taskRepository.save(editedTask);
    }

}
