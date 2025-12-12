import api from './api';

const TaskService = {
  getById: (id) => api.get(`/tasks/${id}`),
  
  update: (id, taskData) => api.put(`/tasks/${id}`, taskData),
  
  partialUpdate: (id, taskData) => api.patch(`/tasks/${id}`, taskData),
  
  delete: (id) => api.delete(`/tasks/${id}`),
  
  getTopPriority: (userId) => api.get('/tasks/top', { params: { userId } })
};

export default TaskService;