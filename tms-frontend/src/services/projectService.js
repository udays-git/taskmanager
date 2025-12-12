import api from './api';

const ProjectService = {
  getAll: (userId) => api.get('/projects', { params: { userId } }),
  
  create: (projectData) => api.post('/projects', projectData),
  
  delete: (projectId, userId) => 
    api.delete(`/projects/${projectId}`, { params: { userId } }),
  
  getTasks: (projectId, userId) => 
    api.get(`/projects/${projectId}/tasks`, { params: { userId } }),
  
  addTask: (projectId, userId, taskData) => 
    api.post(`/projects/${projectId}/tasks`, taskData, { 
      params: { userId } 
    })
};

export default ProjectService;