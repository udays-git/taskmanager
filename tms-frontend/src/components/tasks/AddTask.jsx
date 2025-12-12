import React from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import ProjectService from '../../services/projectService';
import TaskForm from './TaskForm';
import '../../styles/app.css';

function AddTask() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const projectId = searchParams.get('projectId');

  const handleSubmit = async (taskData) => {
    if (!user?.id || !projectId) {
      alert('User ID or Project ID is missing');
      return;
    }

    try {
      await ProjectService.addTask(projectId, user.id, taskData);
      navigate(`/projects/${projectId}/tasks`);
    } catch (err) {
      console.error('Error adding task:', err);
      alert('Failed to add task. Please try again.');
    }
  };

  const handleCancel = () => {
    if (projectId) {
      navigate(`/projects/${projectId}/tasks`);
    } else {
      navigate('/dashboard');
    }
  };

  return (
    <div className="container page-container">
      <div className="add-form-container">
        <TaskForm 
          onSubmit={handleSubmit}
          onCancel={handleCancel}
          initialData={{ projectId }}
        />
      </div>
    </div>
  );
}

export default AddTask;