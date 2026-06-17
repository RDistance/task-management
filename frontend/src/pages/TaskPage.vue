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
        <TaskCard v-for="task in paginatedTasks" :key="task.id" :task="task" @update:task="updateTask" @delete:task="deleteTask" />
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
import { ref, computed, onMounted } from 'vue'
import TaskCard from '../components/TaskCard.vue'
import type { Task } from '../types/task.ts'

// 获取当前环境的 API 基础地址
// 优先使用环境变量，如果没有配置则回退到本地的 localhost
//const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

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
const publishTask = async () => {
  if (!newTask.value.title.trim()) return

  const taskData = {
    title: newTask.value.title.trim(),
    content: newTask.value.content.trim(),
    status: '已发布',
    estimatedCompletionTime: newTask.value.estimatedCompletionTime || undefined
  }

  try {
    const response = await fetch(`/api/tasks/create`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(taskData)
    })
    if (!response.ok) throw new Error('网络请求失败')
    
    // 发布成功后立马请求接口 渲染新数据
    await fetchTasks()
    
    // 重置表单，并自动跳转回第一页查看新任务
    newTask.value.title = ''
    newTask.value.content = ''
    newTask.value.estimatedCompletionTime = ''
    currentPage.value = 1
  } catch (error) {
    console.error('API 请求出错:', error)
    alert('发布任务失败，请检查后端服务是否启动。')
  }
}

// 接收子组件抛出的更新事件，并同步数据
const updateTask = async (updatedTask: Task) => {
  try {
    const response = await fetch(`/api/tasks/update`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(updatedTask)
    })
    if (!response.ok) throw new Error('网络请求失败')
    
    // 更新成功后立马请求接口 刷新数据
    await fetchTasks()
  } catch (error) {
    console.error('API 请求出错:', error)
    alert('更新任务状态失败，请重试。')
  }
};

// 删除任务方法
const deleteTask = async (taskId: string) => {
  try {
    const response = await fetch(`/api/tasks/delete/${taskId}`, {
      method: 'DELETE'
    })
    if (!response.ok) throw new Error('网络请求失败')
    
    // 检查后端是否返回 false
    const success = await response.json()
    if (!success) throw new Error('后端返回 false，任务删除失败（可能已不存在）')
    
    // 删除成功后立马请求接口 刷新数据
    await fetchTasks()
    
    // 处理边界情况：如果当前页的数据被删空了且不是第一页，自动跳回上一页
    if (paginatedTasks.value.length === 0 && currentPage.value > 1) {
      currentPage.value--
    }
  } catch (error) {
    console.error('API 请求出错:', error)
    alert('删除任务失败，请重试。')
  }
}

// 获取任务列表接口
const fetchTasks = async () => {
  try {
    const response = await fetch(`/api/tasks/list`)
    if (!response.ok) throw new Error('获取任务列表失败')
    
    const data = await response.json()
    // 将后端返回的数据映射到 tasks 数组，并且翻转数组让最新创建的任务排在最前面
    tasks.value = data.reverse().map((t: any) => ({
      ...t,
      id: t.id.toString(), // 确保 id 是字符串，适配前端
      date: t.createTime ? t.createTime.replace('T', ' ') : ''
    }))
  } catch (error) {
    console.error('API 请求出错:', error)
  }
}

// 页面加载时自动调用接口
onMounted(() => {
  fetchTasks()
})
</script>

<style scoped>
.task-page {
  width: 900px;
  max-width: 100%;
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

/* --- 响应式 / 移动端兼容样式 --- */

/* 平板及小尺寸桌面 (最大宽度 768px) */
@media (max-width: 768px) {
  .task-grid {
    grid-template-columns: repeat(2, 1fr); /* 平板下改为 2 列 */
  }
  
  .inline-form-group {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .inline-form-group .date-label {
    margin-bottom: 8px; /* 恢复标签下边距 */
  }
}

/* 手机端 (最大宽度 480px) */
@media (max-width: 480px) {
  .task-page {
    padding: 16px; /* 减小手机端页面内边距 */
    width: calc(100% - 32px);
  }
  
  .task-grid {
    grid-template-columns: 1fr; /* 手机下改为单列铺满 */
  }
  
  .submit-btn {
    width: 100%; /* 按钮占满全宽，方便手指点击 */
  }
  
  .pagination {
    flex-direction: column;
    gap: 10px;
  }
}
</style>
