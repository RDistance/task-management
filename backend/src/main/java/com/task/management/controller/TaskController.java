package com.task.management.controller;

import com.task.management.entity.Task;
import com.task.management.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*") // 允许前端跨域请求
public class TaskController {

    @Autowired
    private TaskService taskService;

    // 获取所有任务列表的 GET 接口
    @GetMapping("/list")
    public List<Task> getAllTasks() {
        return taskService.list(); // MyBatis-Plus 提供的默认查询所有数据的方法
    }

    @PostMapping("/create")
    public Task createTask(@RequestBody Task task) {
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        taskService.save(task);
        return task; // MyBatis-Plus 执行完 save 后，会自动将数据库生成的自增 ID 赋给 task 对象
    }

    @PutMapping("/update")
    public boolean updateTask(@RequestBody Task task) {
        task.setUpdateTime(LocalDateTime.now());
        return taskService.updateById(task); // MyBatis-Plus 提供的根据 ID 更新数据的方法
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteTask(@PathVariable Long id) {
        return taskService.removeById(id); // MyBatis-Plus 提供的根据 ID 删除数据的方法
    }
}
