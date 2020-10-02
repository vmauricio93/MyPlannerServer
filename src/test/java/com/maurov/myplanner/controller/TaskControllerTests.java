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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.maurov.myplanner.repository.TaskRepository;
import com.maurov.myplanner.entity.Task;

import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TaskController.class)
public class TaskControllerTests {

    private static String tasksEndpoint;
    private Task taskStub;

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
        taskStub = new Task();
        taskStub.setId(1L);
        taskStub.setDescription("taskStub");
        taskStub.setDone(false);
        taskStub.setDate(LocalDate.now());
        taskStub.setTime(LocalDateTime.now());
        taskStub.setPlace("place");
        taskStub.setTag("tag");
    }

    @Test
    public void shouldGetAListOfTasks() throws Exception {
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
    public void shouldPostATask() throws Exception {
        this.mockMvc
            .perform(
                post(tasksEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content("{ \"description\": \"taskStub\", \"done\": false }")
            ).andExpect(status().isOk());
    }

    @Test
    public void shouldDeleteATask() throws Exception {
        this.mockMvc
            .perform(
                delete(tasksEndpoint + "/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
            ).andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    public void shouldToggleATaskDone(boolean done) throws Exception {
        Task doneTaskStub = new Task();
        when(taskRepository.getOne(anyLong())).thenReturn(taskStub);
        when(taskRepository.save(any())).thenReturn(doneTaskStub);
        
        doneTaskStub.setDone(done);
        this.mockMvc
            .perform(
                put(tasksEndpoint + "/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(
                    "{ \"description\": \"taskStub\", \"done\": " + done + " }"
                )
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.done", is(done)));
    }
    
    @Test
    public void 
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
    
}
