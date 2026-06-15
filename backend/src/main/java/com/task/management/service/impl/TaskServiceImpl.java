package com.task.management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.task.management.entity.Task;
import com.task.management.mapper.TaskMapper;
import com.task.management.service.TaskService;

import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
}
