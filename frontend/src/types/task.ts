export type TaskStatus = '已发布' | '已认领' | '已完成';

export interface Task {
  id: string;
  title: string;
  content: string; // 详情描述
  date: string;    // 发布日期
  status: TaskStatus;
  completionTime?: string; // 完成时间
  estimatedCompletionTime?: string; // 预计完成时间
}
