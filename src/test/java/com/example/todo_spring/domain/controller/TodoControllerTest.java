package com.example.todo_spring.domain.controller;

import com.example.todo_spring.domain.entity.Todo;
import com.example.todo_spring.domain.repository.TodoRepository;
import com.example.todo_spring.domain.service.TodoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    @MockitoBean
    private TodoRepository todoRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("저장 기능 테스트")
    void t1() throws Exception {

        // Given
        Todo todo = new Todo(0, "공부", "상", "진행 중", "메모 없음", LocalDate.of(2025, 1, 1));
        Todo savedTodo = new Todo(1, "공부", "상", "진행 중", "메모 없음", LocalDate.of(2025, 1, 1));
        when(todoService.addTodo(any(Todo.class))).thenReturn(savedTodo);

        // When & Then
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.todo").value("공부"));
    }

    @Test
    @DisplayName("할일 목록 조회 테스트")
    void t2() throws Exception {

        // Given
        Todo todo1 = new Todo(1, "공부", "상", "진행 중", "메모 없음", LocalDate.of(2025, 1, 1));
        Todo todo2 = new Todo(2, "운동", "중", "진행 전", "메모 없음", LocalDate.of(2025, 1, 1));
        when(todoService.getAllTodos()).thenReturn(Arrays.asList(todo1, todo2));

        // When & Then
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].todo").value("공부"));
    }

    @Test
    @DisplayName("한개 할일 조회 테스트")
    void t3() throws Exception {

        // Given
        Todo todo = new Todo(1, "공부", "상", "진행 중", "메모 없음", LocalDate.of(2025, 1, 1));
        when(todoService.getTodoById(1)).thenReturn(todo);

        // When & Then
        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.todo").value("공부"))
                .andExpect(jsonPath("$.priority").value("상"));
    }

    @Test
    @DisplayName("할일 수정 테스트")
    void t4() throws Exception {

        // Given
        Todo updatedTodo = new Todo(0, "스프링 공부", "상", "진행 중", "메모 없음", LocalDate.of(2025, 1, 1));
        Todo savedTodo = new Todo(1, "스프링 공부", "상", "진행 중", "메모 없음", LocalDate.of(2025, 1, 1));
        when(todoService.updateTodo(eq(1), any(Todo.class))).thenReturn(savedTodo);

        // When & Then
        mockMvc.perform(put("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTodo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.todo").value("스프링 공부"))
                .andExpect(jsonPath("$.priority").value("상"));
    }

    @Test
    @DisplayName("할일 삭제 테스트")
    void t5() throws Exception {

        // Given
        when(todoService.deleteTodo(1)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("할일 완료 테스트")
    void t6() throws Exception {

        // Given: 기존 할 일 객체
        Todo todo = new Todo();
        todo.setId(1);
        todo.setTodo("Test Todo");
        todo.setStatus("완료"); // 완료 상태로 변경

        when(todoService.completeTodo(anyInt())).thenReturn(todo);

        // When & Then
        mockMvc.perform(patch("/api/todos/1/complete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.todo").value("Test Todo"))
                .andExpect(jsonPath("$.status").value("완료"));
    }

    @Test
    @DisplayName("마감일 순 정렬 테스트")
    void t7() {

        // Given
        Todo todo1 = new Todo(1, "할 일 1", "상", "진행 중", "메모 1", LocalDate.of(2024, 2, 1));
        Todo todo2 = new Todo(2, "할 일 2", "중", "진행 전", "메모 2", LocalDate.of(2024, 2, 5));

        when(todoRepository.findAllByOrderByDueDateAsc()).thenReturn(Arrays.asList(todo1, todo2));

        TodoService todoService = new TodoService(todoRepository);

        // When
        List<Todo> sortedTodos = todoService.getTodosSortedByDueDate();

        // Then
        assertThat(sortedTodos).hasSize(2);
        assertThat(sortedTodos.get(0).getDueDate()).isBefore(sortedTodos.get(1).getDueDate());
    }

    @Test
    @DisplayName("오늘 마감일 테스트")
    void t8() {

        // Given: 오늘 날짜 데이터 생성
        LocalDate today = LocalDate.now();
        Todo todo1 = new Todo(1, "할 일 1", "상", "진행 중", "메모 1", today);
        Todo todo2 = new Todo(2, "할 일 2", "중", "진행 중", "메모 2", today);

        when(todoRepository.findTodosDueToday(today)).thenReturn(Arrays.asList(todo1, todo2));

        TodoService todoService = new TodoService(todoRepository);

        // When
        List<Todo> todayTodos = todoService.getTodosDueToday();

        // Then
        assertThat(todayTodos).hasSize(2);
        assertThat(todayTodos.get(0).getDueDate()).isEqualTo(today);
        assertThat(todayTodos.get(1).getDueDate()).isEqualTo(today);
    }

    @Test
    @DisplayName("검색 테스트")
    void t9() throws Exception {

        // Given
        String keyword = "공부";
        List<Todo> mockTodos = Arrays.asList(
                new Todo(1, "공부하기", "상", "진행 중", "메모 없음", LocalDate.of(2025, 1, 1)),
                new Todo(2, "자바 공부", "상", "진행 전", "메모 없음", LocalDate.of(2025, 1, 2))
        );

        when(todoService.search(keyword)).thenReturn(mockTodos);

        // When & Then
        mockMvc.perform(get("/api/todos/search")
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].todo").value("공부하기"))
                .andExpect(jsonPath("$[1].todo").value("자바 공부"));
    }
}