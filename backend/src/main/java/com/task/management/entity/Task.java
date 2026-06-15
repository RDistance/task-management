package com.task.management.entity;

import lombok.Data;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

@Data
public class Task {
    
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    private String status;

    //预计完成时间
    private LocalDateTime estimatedCompletionTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
    
    //实际完成时间
    private LocalDateTime completionTime;

}
