import React, { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import TaskService from '../../services/taskService';
import { 
  getStatusColor, 
  getPriorityLevelText, 
  formatDateTime, 
  formatDateLong, 
  isOverdue 
} from '../../utils/helpers';
import '../../styles/app.css';

function TaskDetails() {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  
  const [task, setTask] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [editing, setEditing] = useState(false);
  const [deleting, setDeleting] = useState(false);
  
  const timeoutRef = useRef(null);

  useEffect(() => {
    let mounted = true;
    
    const loadTask = async () => {
      setLoading(true);
      setError('');
      
      try {
        const res = await TaskService.getById(id);
        if (mounted) {
          const taskData = res.data;
          taskData.priority = taskData.priority || 5;
          setTask(taskData);
        }
      } catch (err) {
        if (mounted) {
          console.error(err);
          setError('Failed to load task. It may have been deleted or doesn\'t exist.');
        }
      } finally {
        if (mounted) {
          setLoading(false);
        }
      }
    };

    loadTask();

    return () => { 
      mounted = false;
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
      }
    };
  }, [id]);

  const handleChange = (e) => {
    const { name, value, type } = e.target;
    setTask(prev => ({ 
      ...prev, 
      [name]: type === 'number' ? parseInt(value, 10) : value 
    }));
  };

  const handlePriorityChange = (e) => {
    const value = parseInt(e.target.value, 10);
    setTask(prev => ({ 
      ...prev, 
      priority: value 
    }));
  };

  const handleSave = async (e) => {
    e.preventDefault();
    
    if (task.priority < 1 || task.priority > 10) {
      setError('Priority must be between 1 and 10');
      return;
    }
    
    setSaving(true);
    setError('');
    
    try {
      await TaskService.update(id, task);
      setEditing(false);
      
      setError('Task updated successfully!');
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
      }
      timeoutRef.current = setTimeout(() => setError(''), 3000);
    } catch (err) {
      console.error(err);
      setError('Failed to save changes. Please try again.');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('Are you sure you want to delete this task?')) return;
    
    setDeleting(true);
    try {
      await TaskService.delete(id);
      if (location.state?.from) {
        navigate(location.state.from);
      } else if (task?.projectDTO?.id) {
        navigate(`/projects/${task.projectDTO.id}/tasks`);
      } else {
        navigate('/dashboard');
      }
    } catch (err) {
      console.error(err);
      setError('Failed to delete task. Please try again.');
      setDeleting(false);
    }
  };

  const handleToggleComplete = async () => {
    const newStatus = task.status === 'completed' ? 'pending' : 'completed';
    const previousStatus = task.status;
    
    setTask(prev => ({ ...prev, status: newStatus }));

    try {
      await TaskService.partialUpdate(id, { status: newStatus });
    } catch (err) {
      setTask(prev => ({ ...prev, status: previousStatus }));
      console.error(err);
      setError('Failed to update status. Please try again.');
    }
  };

  const handleCancelEdit = () => {
    setEditing(false);
    setError('');
    
    TaskService.getById(id)
      .then(res => {
        const taskData = res.data;
        taskData.priority = taskData.priority || 5;
        setTask(taskData);
      })
      .catch(err => console.error(err));
  };

  if (loading) return (
    <div className="container page-container">
      <div className="loading">Loading task details...</div>
    </div>
  );
  
  if (!task) return (
    <div className="container page-container">
      <div className="not-found card">
        <h3>Task Not Found</h3>
        <p>The task may have been deleted or doesn't exist.</p>
        <button 
          className="btn btn-secondary mt-3"
          onClick={() => navigate('/dashboard')}
        >
          Back to Dashboard
        </button>
      </div>
    </div>
  );

  const taskCompleted = task.status === 'completed';
  const overdue = isOverdue(task.dueDate, task.status);
  const priority = task.priority || 5;
  const projectId = task.projectDTO?.id;

  return (
    <div className="container page-container">
      <div className="task-details-container card">
        <div className="task-details-header">
          <h1>Task Details</h1>
          <div className="d-flex gap-2">
            {projectId && (
              <button 
                className="btn btn-outline"
                onClick={() => navigate(`/projects/${projectId}/tasks`)}
              >
                ← Back to Tasks
              </button>
            )}
            <button 
              className="btn btn-outline"
              onClick={() => navigate('/dashboard')}
            >
              Dashboard
            </button>
          </div>
        </div>

        {error && (
          <div 
            className={`error-message ${error.includes('successfully') ? 'success' : 'error'}`}
            role="alert"
          >
            {error}
          </div>
        )}

        {!editing ? (
          <div className="task-view">
            <div className="task-header">
              <h2 
                className="task-title"
                style={{ 
                  textDecoration: taskCompleted ? 'line-through' : 'none',
                  opacity: taskCompleted ? 0.7 : 1
                }}
              >
                {task.title}
              </h2>
              <span 
                className="status-badge"
                style={{ backgroundColor: getStatusColor(task.status) }}
              >
                {task.status || 'pending'}
              </span>
            </div>

            <div className="task-info">
              <div className="info-section">
                <h3>Description</h3>
                <p className="description">{task.description || 'No description provided.'}</p>
              </div>

              <div className="info-grid">
                <div className="info-item">
                  <span className="info-label">Priority</span>
                  <div className="d-flex align-center gap-1">
                    <span className={`priority-badge priority-${priority}`}>
                      <span className="priority-number">{priority}</span>
                    </span>
                    <span style={{ color: '#64748b', fontSize: '14px' }}>
                      ({getPriorityLevelText(priority)})
                    </span>
                  </div>
                </div>
                <div className="info-item">
                  <span className="info-label">Due Date</span>
                  <span className={`due-date ${overdue ? 'overdue' : ''}`}>
                    {formatDateLong(task.dueDate)}
                    {overdue && (
                      <span className="overdue-badge">Overdue</span>
                    )}
                  </span>
                </div>
                <div className="info-item">
                  <span className="info-label">Project</span>
                  <span>
                    {task.projectDTO?.title || 'No project'}
                  </span>
                </div>
                <div className="info-item">
                  <span className="info-label">Created</span>
                  <span>
                    {formatDateTime(task.createdAt)}
                  </span>
                </div>
                <div className="info-item">
                  <span className="info-label">Last Updated</span>
                  <span>
                    {formatDateTime(task.updatedAt)}
                  </span>
                </div>
              </div>
            </div>

            <div className="action-buttons">
              <button 
                className="btn btn-primary"
                onClick={() => setEditing(true)}
              >
                Edit Task
              </button>
              <button 
                className={`btn ${taskCompleted ? 'btn-warning' : 'btn-success'}`}
                onClick={handleToggleComplete}
              >
                {taskCompleted ? 'Mark as Incomplete' : 'Mark as Complete'}
              </button>
              <button 
                className="btn btn-danger"
                onClick={handleDelete}
                disabled={deleting}
              >
                {deleting ? 'Deleting...' : 'Delete Task'}
              </button>
            </div>
          </div>
        ) : (
          <form className="task-edit-form" onSubmit={handleSave}>
            <h2>Edit Task</h2>
            
            <div className="form-group">
              <label htmlFor="title">Title *</label>
              <input 
                id="title"
                name="title" 
                value={task.title || ''} 
                onChange={handleChange} 
                required 
                placeholder="Enter task title"
                className="form-control"
              />
            </div>

            <div className="form-group">
              <label htmlFor="description">Description</label>
              <textarea 
                id="description"
                name="description" 
                value={task.description || ''} 
                onChange={handleChange} 
                rows="4"
                placeholder="Enter task description"
                className="form-control"
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="status">Status</label>
                <select 
                  id="status"
                  name="status" 
                  value={task.status || 'pending'} 
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
                  name="dueDate" 
                  type="date" 
                  value={task.dueDate ? task.dueDate.split('T')[0] : ''} 
                  onChange={handleChange} 
                  className="form-control"
                />
              </div>
            </div>

            <div className="form-group priority-slider-container">
              <label htmlFor="priority">
                Priority: <strong>{priority}</strong>
              </label>
              <input
                id="priority"
                type="range"
                name="priority"
                min="1"
                max="10"
                value={priority}
                onChange={handlePriorityChange}
                className="priority-slider"
              />
              <div className="priority-scale">
                <span className="priority-scale-item">1</span>
                <span className="priority-scale-item">2</span>
                <span className="priority-scale-item">3</span>
                <span className="priority-scale-item">4</span>
                <span className="priority-scale-item">5</span>
                <span className="priority-scale-item">6</span>
                <span className="priority-scale-item">7</span>
                <span className="priority-scale-item">8</span>
                <span className="priority-scale-item">9</span>
                <span className="priority-scale-item">10</span>
              </div>
              <span className="form-hint">
                Current: {priority} - {getPriorityLevelText(priority)}
              </span>
            </div>

            <div className="form-actions">
              <button 
                type="submit" 
                className="btn btn-success"
                disabled={saving}
              >
                {saving ? 'Saving...' : 'Save Changes'}
              </button>
              <button 
                type="button" 
                className="btn btn-outline"
                onClick={handleCancelEdit}
              >
                Cancel
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}

export default TaskDetails;