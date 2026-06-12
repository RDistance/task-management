package com.task.management;

import com.task.management.entity.Task;
import com.task.management.mapper.TaskMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private TaskMapper taskMapper;

    @Test
    void testSelect() {
        List<Task> list = taskMapper.selectList(null);
        System.out.println(list);
    }
}
