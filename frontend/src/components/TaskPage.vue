<template>
  <div class="task-page">
    <!-- 发布任务区域 -->
    <section class="publish-section">
      <h2>发布新任务</h2>
      <div class="form-group">
        <input v-model="newTask.title" type="text" placeholder="请输入任务标题" />
      </div>
      <div class="form-group">
        <textarea v-model="newTask.content" placeholder="请输入任务详情..." rows="3"></textarea>
      </div>
      <div class="form-group inline-form-group">
        <label for="estimatedTime" class="date-label">预计完成时间：</label>
        <input id="estimatedTime" v-model="newTask.estimatedCompletionTime" type="datetime-local" />
      </div>
      <button class="submit-btn" @click="publishTask" :disabled="!newTask.title.trim()">
        发布任务
      </button>
    </section>

    <!-- 任务列表展示区域 -->
    <section class="tasks-section">
      <h2>任务列表</h2>
      
      <!-- 空状态 -->
      <div v-if="paginatedTasks.length === 0" class="empty-state">
        暂无任务，快去发布一个吧！
      </div>

      <!-- 任务方块网格 -->
      <div v-else class="task-grid">
        <TaskCard v-for="task in paginatedTasks" :key="task.id" :task="task" @update:task="updateTask" />
      </div>

      <!-- 分页控件 -->
      <div v-if="totalPages > 1" class="pagination">
        <button @click="currentPage--" :disabled="currentPage === 1">上一页</button>
        <span class="page-info">第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
        <button @click="currentPage++" :disabled="currentPage === totalPages">下一页</button>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import TaskCard from './TaskCard.vue'
import type { Task } from '../types/task'


// 状态定义
const tasks = ref<Task[]>([])
const newTask = ref({ title: '', content: '', estimatedCompletionTime: '' })

// 分页设置
const currentPage = ref(1)
const pageSize = 6 // 每页显示 6 个方块任务

// 计算属性：总页数
const totalPages = computed(() => Math.ceil(tasks.value.length / pageSize) || 1)

// 计算属性：当前页展示的任务
const paginatedTasks = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  const end = start + pageSize
  return tasks.value.slice(start, end)
})

// 发布任务方法
const publishTask = () => {
  if (!newTask.value.title.trim()) return

  const task: Task = {
    id: Date.now().toString(),
    title: newTask.value.title.trim(),
    content: newTask.value.content.trim(),
    date: new Date().toLocaleString(),
    status: '已发布',
    estimatedCompletionTime: newTask.value.estimatedCompletionTime || undefined
  }

  // 将新任务插入到最前面
  tasks.value.unshift(task)
  
  // 重置表单，并自动跳转回第一页查看新任务
  newTask.value.title = ''
  newTask.value.content = ''
  newTask.value.estimatedCompletionTime = ''
  currentPage.value = 1
}

// 接收子组件抛出的更新事件，并同步数据
const updateTask = (updatedTask: Task) => {
  const index = tasks.value.findIndex(t => t.id === updatedTask.id);
  if (index !== -1) {
    tasks.value[index] = updatedTask;
  }
};

// 生成一些初始的测试数据以便于观察分页和方块布局
const now = new Date();
const tomorrow = new Date(now);
tomorrow.setDate(tomorrow.getDate() + 1);
// 转成本地时间的 YYYY-MM-DDTHH:mm 格式，以便 datetime-local 输入框能正确识别
const defaultEstimatedTime = new Date(tomorrow.getTime() - (tomorrow.getTimezoneOffset() * 60000)).toISOString().slice(0, 16);

for (let i = 1; i <= 15; i++) {
  tasks.value.push({
    id: `${i}`,
    title: `测试任务 ${i}`,
    content: `这是系统自动生成的第 ${i} 个测试任务的详情描述内容。你可以在这里看到多行文本的截断效果。`,
    date: new Date().toLocaleString(),
    status: '已发布',
    estimatedCompletionTime: defaultEstimatedTime
  })
}
</script>

<style scoped>
.task-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  font-family: system-ui, -apple-system, sans-serif;
}

.publish-section {
  background: #f8f9fa;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.form-group {
  margin-bottom: 16px;
}

input, textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  box-sizing: border-box;
  font-size: 14px;
  transition: border-color 0.2s;
}

input:focus, textarea:focus {
  outline: none;
  border-color: #42b883;
}

.date-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #333;
  min-width: 100px;
}

.inline-form-group {
  display: flex;
  align-items: center; /* 垂直居中对齐 */
  gap: 10px; /* 标签和输入框之间的间距 */
  /* 保持原有的 margin-bottom，或者根据需要调整 */
}
.inline-form-group .date-label {
  display: inline-block; /* 覆盖原来的 display: block */
  margin-bottom: 0; /* 移除底部外边距，因为它们现在在同一行 */
}
.submit-btn {
  padding: 10px 24px;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.submit-btn:hover:not(:disabled) {
  background-color: #33a06f;
}

.submit-btn:disabled {
  background-color: #a8d5c2;
  cursor: not-allowed;
}

.empty-state {
  text-align: center;
  color: #888;
  padding: 40px 0;
}

/* 任务方块核心布局 */
.task-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 40px;
}

.pagination button {
  padding: 8px 16px;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  color: #333;
}

.pagination button:hover:not(:disabled) {
  border-color: #42b883;
  color: #42b883;
}

.pagination button:disabled {
  background-color: #f5f5f5;
  color: #ccc;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #666;
}
</style>
