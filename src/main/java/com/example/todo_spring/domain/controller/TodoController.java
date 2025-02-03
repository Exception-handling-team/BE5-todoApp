package com.example.todo_spring.domain.controller;

import com.example.todo_spring.domain.entity.Todo;
import com.example.todo_spring.domain.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {

        List<Todo> todos = todoService.getAllTodos();
        return ResponseEntity.ok(todos);
    }

    @PostMapping
    public ResponseEntity<Todo> addTodo(@Valid @RequestBody Todo todo) {

        Todo savedTodo = todoService.addTodo(todo);
        return ResponseEntity.ok(savedTodo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable int id) {

        Todo todo = todoService.getTodoById(id);
        return todo != null ? ResponseEntity.ok(todo) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@Valid @PathVariable int id, @RequestBody Todo todo) {

        Todo updatedTodo = todoService.updateTodo(id, todo);
        return updatedTodo != null ? ResponseEntity.ok(updatedTodo) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable int id) {

        boolean deleted = todoService.deleteTodo(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Todo> completeTodo(@PathVariable int id) {

        Todo todo = todoService.completeTodo(id);
        return todo != null ? ResponseEntity.ok(todo) : ResponseEntity.notFound().build();
    }

    @GetMapping("/due-today")
    public ResponseEntity<List<Todo>> getTodosDueToday() {

        List<Todo> todos = todoService.getTodosDueToday();
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Todo>> searchTodos(@RequestParam String keyword) {

        List<Todo> todos = todoService.search(keyword);
        return todos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(todos);
    }
}
