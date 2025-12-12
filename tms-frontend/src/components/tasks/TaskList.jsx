import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import ProjectService from '../../services/projectService';
import TaskService from '../../services/taskService';
import { getPriorityLevelText, formatDateLong } from '../../utils/helpers';
import '../../styles/app.css';

function TaskList() {
  const { projectId } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  
  const [tasks, setTasks] = useState([]);
  const [project, setProject] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filterMode, setFilterMode] = useState('all');
  const [filterLoading, setFilterLoading] = useState(false);

  useEffect(() => {
    if (!user) {
      navigate('/login');
    } else {
      loadProjectAndTasks();
    }
  }, [projectId, user, navigate]);

  const loadProjectAndTasks = async () => {
    setLoading(true);
    setError('');
    
    try {
      // Load project details
      const projectsRes = await ProjectService.getAll(user.id);
      const currentProject = projectsRes.data.find(p => p.id.toString() === projectId);
      setProject(currentProject || null);
      
      // Load tasks for this project
      const tasksRes = await ProjectService.getTasks(projectId, user.id);
      const tasksWithNumericPriority = tasksRes.data.map(task => ({
        ...task,
        priority: task.priority || 5
      }));
      setTasks(tasksWithNumericPriority);
      setFilterMode('all');
    } catch (err) {
      console.error('Error loading tasks:', err);
      setError('Failed to load tasks. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleTopPriorityClick = async () => {
    if (filterMode === 'top5') {
      setFilterMode('all');
      await loadProjectAndTasks();
      return;
    }

    setFilterLoading(true);
    try {
      const res = await TaskService.getTopPriority(user.id);
      const topTasks = res.data.map(task => ({
        ...task,
        priority: task.priority || 5
      }));
      setTasks(topTasks);
      setFilterMode('top5');
      setError('');
    } catch (err) {
      console.error(err);
      setError('Failed to fetch top priority tasks. Please try again.');
    } finally {
      setFilterLoading(false);
    }
  };

  const handleShowAll = async () => {
    setFilterMode('all');
    await loadProjectAndTasks();
  };

  const handleDeleteTask = async (taskId) => {
    if (!window.confirm('Are you sure you want to delete this task?')) return;
    
    try {
      await TaskService.delete(taskId);
      loadProjectAndTasks();
    } catch (err) {
      console.error('Error deleting task:', err);
      setError('Failed to delete task. Please try again.');
    }
  };

  const getFilteredTaskCount = () => {
    if (filterMode === 'top5') {
      return `${tasks.length} of top priority`;
    }
    return tasks.length;
  };

  if (loading) return (
    <div className="container page-container">
      <div className="loading">Loading tasks...</div>
    </div>
  );
  
  if (!project) return (
    <div className="container page-container">
      <div className="not-found card">
        <h3>Project Not Found</h3>
        <p>The project may have been deleted or doesn't exist.</p>
        <button 
          className="btn btn-secondary mt-3"
          onClick={() => navigate('/dashboard')}
        >
          Back to Projects
        </button>
      </div>
    </div>
  );

  return (
    <div className="container page-container">
      <div className="task-list-container">
        <div className="task-list-header">
          <div>
            <h1>{project.title} - Tasks ({getFilteredTaskCount()})</h1>
            <p style={{ color: '#64748b', fontSize: '14px', marginTop: '4px' }}>
              {project.description || 'No description'}
            </p>
          </div>
          <div className="task-list-actions">
            <div className="d-flex gap-2">
              {filterMode === 'all' ? (
                <button 
                  className="btn btn-secondary"
                  onClick={handleTopPriorityClick}
                  disabled={filterLoading}
                >
                  {filterLoading ? 'Loading...' : 'Top 5 Priority'}
                </button>
              ) : (
                <button 
                  className="btn btn-outline"
                  onClick={handleShowAll}
                  disabled={filterLoading}
                >
                  {filterLoading ? 'Loading...' : 'Show All'}
                </button>
              )}
              <Link 
                to={`/tasks/add?projectId=${projectId}`}
                className="btn btn-primary"
              >
                Add Task
              </Link>
              <button 
                className="btn btn-outline"
                onClick={() => navigate('/dashboard')}
              >
                Back to Projects
              </button>
            </div>
          </div>
        </div>

        {error && (
          <div className="error-message error" role="alert">
            {error}
          </div>
        )}

        {tasks.length === 0 ? (
          <div className="empty-state">
            <p>No tasks found in this project. {filterMode === 'top5' ? 'Try showing all tasks.' : 'Add your first task!'}</p>
            <Link 
              to={`/tasks/add?projectId=${projectId}`}
              className="btn btn-primary mt-2"
            >
              Add First Task
            </Link>
          </div>
        ) : (
          <>
            <div className="priority-legend mb-3">
              <div className="d-flex gap-2 align-center">
                <div className="d-flex align-center gap-1">
                  <div className="priority-badge priority-1" style={{width: '12px', height: '12px', borderRadius: '2px'}}></div>
                  <span style={{fontSize: '12px', color: '#64748b'}}>Low (1-3)</span>
                </div>
                <div className="d-flex align-center gap-1">
                  <div className="priority-badge priority-5" style={{width: '12px', height: '12px', borderRadius: '2px'}}></div>
                  <span style={{fontSize: '12px', color: '#64748b'}}>Medium (4-6)</span>
                </div>
                <div className="d-flex align-center gap-1">
                  <div className="priority-badge priority-9" style={{width: '12px', height: '12px', borderRadius: '2px'}}></div>
                  <span style={{fontSize: '12px', color: '#64748b'}}>High (7-10)</span>
                </div>
                {filterMode === 'top5' && (
                  <div className="filter-badge">
                    <span style={{fontSize: '12px', color: '#3b82f6', fontWeight: '600'}}>
                      Showing Top Priority
                    </span>
                  </div>
                )}
              </div>
            </div>

            <ul className="tasks-grid">
              {tasks.map(task => (
                <li key={task.id} className="task-card">
                  <Link 
                    to={`/tasks/${task.id}`} 
                    className="task-link"
                  >
                    <div className="task-header">
                      <strong className="task-title">{task.title}</strong>
                      <span className={`priority-badge priority-${task.priority || 5}`}>
                        <span className="priority-number">{task.priority || 5}</span>
                      </span>
                    </div>
                    <div className="task-details">
                      <span className="task-status">Status: {task.status || 'pending'}</span>
                      {task.dueDate && (
                        <span className="due-date">
                          Due: {formatDateLong(task.dueDate)}
                        </span>
                      )}
                    </div>
                    <div className="priority-level" style={{fontSize: '12px', color: '#666', marginTop: '8px'}}>
                      {getPriorityLevelText(task.priority)}
                    </div>
                  </Link>
                  <div className="mt-2">
                    <button 
                      className="btn btn-danger btn-sm"
                      onClick={(e) => {
                        e.preventDefault();
                        e.stopPropagation();
                        handleDeleteTask(task.id);
                      }}
                    >
                      Delete
                    </button>
                  </div>
                </li>
              ))}
            </ul>

            {filterMode === 'top5' && tasks.length > 0 && (
              <div className="d-flex justify-center mt-4">
                <button 
                  className="btn btn-outline"
                  onClick={handleShowAll}
                  disabled={filterLoading}
                >
                  {filterLoading ? 'Loading...' : '← Back to All Tasks'}
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}

export default TaskList;