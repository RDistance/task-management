package com.task.management.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Task {

    private Long id;

    private String title;

    private String content;

    private String status;

    private LocalDateTime estimatedCompletionTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
