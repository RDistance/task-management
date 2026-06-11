<script setup lang="ts">
import { ref } from 'vue';
import type { Task, TaskStatus } from '../types/task';

const props = defineProps<{
  task: Task;
}>();

const emit = defineEmits<{
  (e: 'update:task', updatedTask: Task): void;
}>();

const isEditing = ref(false);
const editStatus = ref<TaskStatus>(props.task.status);
const editCompletionTime = ref(props.task.completionTime || '');
const editEstimatedTime = ref(props.task.estimatedCompletionTime || '');

const handleClaim = () => {
  emit('update:task', { ...props.task, status: '已认领' });
};

const handleComplete = () => {
  const now = new Date();
  const formattedTime = new Date(now.getTime() - (now.getTimezoneOffset() * 60000))
    .toISOString()
    .slice(0, 16);
    
  emit('update:task', { ...props.task, status: '已完成', completionTime: formattedTime });
};

const saveEdit = () => {
  emit('update:task', {
    ...props.task,
    status: editStatus.value,
    completionTime: editCompletionTime.value,
    estimatedCompletionTime: editEstimatedTime.value,
  });
  isEditing.value = false;
};

const cancelEdit = () => {
  editStatus.value = props.task.status;
  editCompletionTime.value = props.task.completionTime || '';
  editEstimatedTime.value = props.task.estimatedCompletionTime || '';
  isEditing.value = false;
};
</script>

<template>
  <div class="task-card">
    <div class="header">
      <h3 class="task-title" :title="task.title">{{ task.title }}</h3>
      <span class="status-badge" :class="task.status">{{ task.status }}</span>
    </div>
    
    <div class="content-wrapper">
      <p class="task-content">{{ task.content }}</p>
    </div>
    
    <div v-if="!isEditing" class="view-mode">
      <div class="time-info">
        <p class="task-date">发布于: {{ task.date }}</p>
        <p v-if="task.estimatedCompletionTime" class="task-date">预计完成: {{ task.estimatedCompletionTime.replace('T', ' ') }}</p>
        <p v-if="task.completionTime" class="task-date">完成于: {{ task.completionTime.replace('T', ' ') }}</p>
      </div>
      
      <div class="actions">
        <button v-if="task.status === '已发布'" @click="handleClaim" class="btn-primary">认领</button>
        <button v-if="task.status === '已认领'" @click="handleComplete" class="btn-success">标记完成</button>
        <button @click="isEditing = true" class="btn-default">编辑</button>
      </div>
    </div>

    <div v-else class="edit-mode">
      <div class="form-group">
        <label>预计完成时间:</label>
        <input type="datetime-local" v-model="editEstimatedTime" />
      </div>
      <div class="form-group">
        <label>状态:</label>
        <select v-model="editStatus">
          <option value="已发布">已发布</option>
          <option value="已认领">已认领</option>
          <option value="已完成">已完成</option>
        </select>
      </div>
      <div class="form-group" v-if="editStatus === '已完成'">
        <label>完成时间:</label>
        <input type="datetime-local" v-model="editCompletionTime" />
      </div>
      <div class="actions edit-actions">
        <button @click="saveEdit" class="btn-primary">保存</button>
        <button @click="cancelEdit" class="btn-default">取消</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.task-card {
  background: #ffffff;
  border: 1px solid #eaeaea;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  height: 100%;
  box-sizing: border-box;
  transition: transform 0.2s, box-shadow 0.2s;
}
.task-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08); }
.header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px; gap: 8px; }
.task-title { margin: 0; font-size: 18px; color: #2c3e50; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; flex: 1; }
.status-badge { padding: 4px 8px; border-radius: 4px; font-size: 0.8rem; font-weight: bold; white-space: nowrap; }
.status-badge.已发布 { background-color: #e2e8f0; color: #475569; }
.status-badge.已认领 { background-color: #fef08a; color: #854d0e; }
.status-badge.已完成 { background-color: #bbf7d0; color: #166534; }
.content-wrapper { flex: 1; margin-bottom: 16px; }
.task-content { color: #666; font-size: 14px; line-height: 1.5; margin: 0; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden; }
.time-info { border-top: 1px solid #f0f0f0; padding-top: 12px; margin-bottom: 12px; }
.task-date { font-size: 12px; color: #a0a0a0; margin: 0 0 4px 0; }
.actions { display: flex; gap: 8px; }
.edit-actions { margin-top: 12px; }
button { padding: 6px 12px; border: none; border-radius: 4px; cursor: pointer; font-size: 0.85rem; flex: 1; }
.btn-primary { background-color: #42b883; color: white; }
.btn-success { background-color: #22c55e; color: white; }
.btn-default { background-color: #e2e8f0; color: #334155; }
button:hover { opacity: 0.9; }
button:disabled { cursor: not-allowed; opacity: 0.5; }
.form-group { margin-bottom: 12px; display: flex; flex-direction: column; gap: 4px; }
.form-group label { font-size: 0.9rem; color: #334155; }
.form-group select, .form-group input { padding: 6px; border: 1px solid #cbd5e1; border-radius: 4px; font-family: inherit; }
</style>
