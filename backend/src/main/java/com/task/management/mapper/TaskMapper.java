package com.task.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.task.management.entity.Task;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
}
