package com.maurov.myplanner.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.maurov.myplanner.repository.TaskRepository;
import com.maurov.myplanner.dto.TaskDTO;
import com.maurov.myplanner.entity.Task;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TaskController.class)
public class TaskControllerTests {

    private static String tasksEndpoint;
    private TaskDTO taskDTOStub;
    private Task taskStub;

    private static final ModelMapper MODEL_MAPPER = new ModelMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskRepository taskRepository;

    @BeforeAll
    public static void setup() {
        tasksEndpoint = "/api/v1/tasks";
    }

    @BeforeEach
    public void init() {
        taskDTOStub = new TaskDTO();
        taskDTOStub.setId(1L);
        taskDTOStub.setDescription("taskStub");
        taskDTOStub.setDone(false);
        taskDTOStub.setDate(LocalDate.now());
        taskDTOStub.setTime(OffsetDateTime.now());
        taskDTOStub.setPlace("place");
        taskDTOStub.setTag("tag");
        taskStub = MODEL_MAPPER.map(taskDTOStub, Task.class);
    }

    @Test
    void shouldGetAListOfTasks() throws Exception {
        List<Task> listOfTasksStub = Arrays.asList(taskStub);
        when(taskRepository.findAll()).thenReturn(listOfTasksStub);

        this.mockMvc
            .perform(get(tasksEndpoint))
            .andExpect(status().isOk())
            .andExpect(content().json(
                "[{ \"description\": \"taskStub\", \"done\": false }]")
            );
    }

    @Test
    void shouldPostATask() throws Exception {
        this.mockMvc
            .perform(
                post(tasksEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("{ \"description\": \"taskStub\", \"done\": false }")
            ).andExpect(status().isOk());
    }

    @Test
    void shouldDeleteATask() throws Exception {
        this.mockMvc
            .perform(
                delete(tasksEndpoint + "/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
            ).andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldToggleATaskDone(boolean done) throws Exception {
        Task doneTaskStub = new Task();
        when(taskRepository.findById(anyLong()))
            .thenReturn(Optional.of(taskStub));
        when(taskRepository.save(any())).thenReturn(doneTaskStub);
        
        doneTaskStub.setDone(done);
        this.mockMvc
            .perform(
                put(tasksEndpoint + "/{id}", 1L)
                .param("action", "toggleAsDone")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(
                    "{ \"description\": \"taskStub\", \"done\": " + done + " }"
                )
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.done", is(done)));
    }
    
    @Test
    void 
    shouldReturnBadRequestCodeIfNoDescriptionIsGivenWhenPostingATask()
    throws Exception {
        this.mockMvc
            .perform(
                post(tasksEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("{ \"description\": \" \", \"done\": false }")
            ).andExpect(status().isBadRequest());
    }

    @Test
    void shouldEditATask() throws Exception {
        Task editedTaskStub = new Task();
        editedTaskStub.setDescription("New Task Stub description");
        when(taskRepository.findById(anyLong()))
            .thenReturn(Optional.of(taskStub));
        when(taskRepository.save(any())).thenReturn(editedTaskStub);

        editedTaskStub.setDescription("Edited description");
        this.mockMvc
            .perform(
                put(tasksEndpoint + "/{id}", 1L)
                .param("action", "edit")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(
                    "{ \"description\":\"Edited description\", \"done\": true }"
                )
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.description", is("Edited description")));
    }

    @Test
    void shouldThrowAnExceptionIfTaskDoesNotExist() throws Exception {
        when(taskRepository.findById(anyLong()))
            .thenReturn(Optional.empty());
                
        this.mockMvc
            .perform(
                put(tasksEndpoint + "/{id}", 1L)
                .param("action", "edit")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(
                    "{ \"description\": \"Task without ID\", \"done\": false }"
                )
            )
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof ResponseStatusException))
        .andExpect(status().isNotFound());     
    }

    @Test
    void shouldReturnSameTaskIfNoActionIsPresentInURLParams() throws Exception {
        when(taskRepository.findById(anyLong()))
            .thenReturn(Optional.of(taskStub));
        when(taskRepository.save(any())).thenReturn(taskStub);

        this.mockMvc
            .perform(
                put(tasksEndpoint + "/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(
                    "{ \"description\": \"anotherTaskStub\", \"done\": true }"
                )
            )
        .andExpect(status().isOk())
        .andExpect(content().json(
            "{ \"description\": \"taskStub\", \"done\": false }"
        ));
    }

    @Test
    void shouldThrowAnExceptionIfAnInvalidActionIsPresentInUrlParams()
    throws Exception {
        when(taskRepository.findById(anyLong()))
            .thenReturn(Optional.of(taskStub));
        
        this.mockMvc
            .perform(
                put(tasksEndpoint + "/{id}", 1L)
                .param("action", "invalidAction")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(
                    "{ \"description\": \"anotherTaskStub\", \"done\": true }"
                )
            )
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof ResponseStatusException))
        .andExpect(status().isBadRequest());
    }
    
}
