package com.example.todo_spring.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Todo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String todo;
    @Pattern(regexp = "상|중|하", message = "우선순위(priority)는 '상', '중', '하' 중 하나여야 합니다.")
    private String priority;
    @Pattern(regexp = "진행 전|진행 중|완료", message = "상태(status)는 '진행 전', '진행 중' 중 하나여야 합니다.")
    private String status;
    private String notes;
    private LocalDate dueDate;
}
