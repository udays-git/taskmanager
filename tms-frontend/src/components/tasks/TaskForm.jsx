import React, { useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import '../../styles/app.css';

function TaskForm({ onSubmit, onCancel, initialData = {} }) {
  const [searchParams] = useSearchParams();
  const projectId = searchParams.get('projectId') || initialData.projectId;
  
  const [formData, setFormData] = useState({
    title: initialData.title || '',
    description: initialData.description || '',
    status: initialData.status || 'pending',
    dueDate: initialData.dueDate || '',
    priority: initialData.priority || 5,
    projectId: projectId
  });

  const [error, setError] = useState('');

  const handleChange = (e) => {
    const { name, value, type } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'number' ? parseInt(value, 10) : value
    }));
    setError('');
  };

  const handlePriorityChange = (e) => {
    const value = parseInt(e.target.value, 10);
    setFormData(prev => ({
      ...prev,
      priority: value
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (!formData.title.trim()) {
      setError('Task title is required');
      return;
    }
    
    if (formData.priority < 1 || formData.priority > 10) {
      setError('Priority must be between 1 and 10');
      return;
    }
    
    if (!formData.projectId) {
      setError('Project ID is required');
      return;
    }
    
    onSubmit(formData);
  };

  return (
    <div className="card">
      <h2>{initialData.id ? 'Edit Task' : 'Add New Task'}</h2>
      
      {error && (
        <div className="error-message error" role="alert">
          {error}
        </div>
      )}
      
      <form className="add-form" onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="title">Title *</label>
          <input
            id="title"
            type="text"
            name="title"
            value={formData.title}
            onChange={handleChange}
            placeholder="Enter task title"
            className="form-control"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="description">Description</label>
          <textarea
            id="description"
            name="description"
            value={formData.description}
            onChange={handleChange}
            placeholder="Enter task description"
            className="form-control"
            rows="4"
          />
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="status">Status</label>
            <select
              id="status"
              name="status"
              value={formData.status}
              onChange={handleChange}
              className="form-control"
            >
              <option value="pending">Pending</option>
              <option value="in-progress">In Progress</option>
              <option value="completed">Completed</option>
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="dueDate">Due Date</label>
            <input
              id="dueDate"
              type="date"
              name="dueDate"
              value={formData.dueDate}
              onChange={handleChange}
              className="form-control"
            />
          </div>
        </div>

        <div className="form-group priority-slider-container">
          <label htmlFor="priority">Priority: {formData.priority}</label>
          <input
            id="priority"
            type="range"
            name="priority"
            min="1"
            max="10"
            value={formData.priority}
            onChange={handlePriorityChange}
            className="priority-slider"
          />
          <div className="priority-scale">
            <span className="priority-scale-item">1 (Low)</span>
            <span className="priority-scale-item">2</span>
            <span className="priority-scale-item">3</span>
            <span className="priority-scale-item">4</span>
            <span className="priority-scale-item">5</span>
            <span className="priority-scale-item">6</span>
            <span className="priority-scale-item">7</span>
            <span className="priority-scale-item">8</span>
            <span className="priority-scale-item">9</span>
            <span className="priority-scale-item">10 (High)</span>
          </div>
          <span className="form-hint">
            Drag slider to set priority from 1 (lowest) to 10 (highest)
          </span>
        </div>

        <div className="form-actions">
          <button type="submit" className="btn btn-primary w-100">
            {initialData.id ? 'Update Task' : 'Add Task'}
          </button>
          {onCancel && (
            <button 
              type="button" 
              className="btn btn-outline w-100"
              onClick={onCancel}
            >
              Cancel
            </button>
          )}
        </div>
      </form>
    </div>
  );
}

export default TaskForm;