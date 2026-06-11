<script setup lang="ts">
import { ref } from 'vue';
import TaskCard from './TaskCard.vue';
import type { Task } from '../types/task';

// 模拟后端获取的任务列表数据
const tasks = ref<Task[]>([
  { id: '1', title: '前端页面开发', content: '完成任务列表与卡片的编写', status: '已发布', date: "2026-06-11 10:00", estimatedCompletionTime: "2026-06-12T18:00" },
  { id: '2', title: '登录功能联调', content: '等后续加上登录系统后与后端联调接口', status: '已认领', date: "2026-06-11 10:00", estimatedCompletionTime: "2026-06-15T18:00" },
  { id: '3', title: '项目初始化', content: '基于 Vite + Vue 3 构建项目架构', status: '已完成', date: "2026-06-11 10:00", completionTime: "2026-06-11T10:00" },
]);

// 接收子组件抛出的更新事件，并同步数据
const updateTask = (updatedTask: Task) => {
  const index = tasks.value.findIndex(t => t.id === updatedTask.id);
  if (index !== -1) {
    tasks.value[index] = updatedTask;
  }
};
</script>

<template>
  <div class="task-list">
    <h2>任务看板</h2>
    <TaskCard 
      v-for="task in tasks" 
      :key="task.id" 
      :task="task" 
      @update:task="updateTask"
    />
  </div>
</template>

<style scoped>
.task-list {
  max-width: 600px;
}
</style>
