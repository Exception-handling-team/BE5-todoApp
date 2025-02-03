package com.example.todo_spring.domain.repository;

import com.example.todo_spring.domain.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Integer> {
    List<Todo> findAllByOrderByDueDateAsc();

    @Query("SELECT t FROM Todo t WHERE t.dueDate = :today")
    List<Todo> findTodosDueToday(LocalDate today);

    List<Todo> findByTodoContainingIgnoreCase(String keyword);
}
