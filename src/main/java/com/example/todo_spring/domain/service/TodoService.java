package com.example.todo_spring.domain.service;

import com.example.todo_spring.domain.entity.Todo;
import com.example.todo_spring.domain.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    public Todo addTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    public Todo getTodoById(int id) {
        return todoRepository.findById(id).orElse(null);
    }

    public Todo updateTodo(int id, Todo todo) {

        if (todoRepository.existsById(id)) {
            todo.setId(id);
            return todoRepository.save(todo);
        }
        return null;
    }

    public boolean deleteTodo(int id) {

        if (todoRepository.existsById(id)) {
            todoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Todo completeTodo(int id) {

        Optional<Todo> todoOptional = todoRepository.findById(id);
        if (todoOptional.isPresent()) {

            Todo todo = todoOptional.get();
            todo.setStatus("완료");  // 상태를 변경

            return todoRepository.save(todo);
        }
        return null;
    }

    public List<Todo> getTodosSortedByDueDate() {
        return todoRepository.findAllByOrderByDueDateAsc();
    }

    public List<Todo> getTodosDueToday() {

        LocalDate today = LocalDate.now();
        return todoRepository.findTodosDueToday(today);
    }

    public List<Todo> search(String keyword) {
        return todoRepository.findByTodoContainingIgnoreCase(keyword);
    }
}
