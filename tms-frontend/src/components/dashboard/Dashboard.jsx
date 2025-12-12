import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ProjectService from '../../services/projectService';
import ProjectForm from './ProjectForm';
import ProjectCard from './ProjectCard';
import { useAuth } from '../../context/authContext';

export default function Dashboard() {
  const { user, logout, loading } = useAuth();
  const [projects, setProjects] = useState([]);
  const [loadingProjects, setLoadingProjects] = useState(false);
  const [showNew, setShowNew] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (loading) return;
    if (!user) {
      navigate('/login');
    } else {
      load();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user, loading]);

  async function load() {
    setLoadingProjects(true);
    setError(null);
    try {
      const response = await ProjectService.getAll(user.id);
      setProjects(response.data);
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Failed to load projects');
    }
    setLoadingProjects(false);
  }

  async function handleCreate(project) {
    try {
      const dto = { ...project, userId: user.id };
      await ProjectService.create(dto);
      setShowNew(false);
      load();
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Failed to create project');
    }
  }

  async function handleDelete(id) {
    if (!window.confirm('Are you sure you want to delete this project?')) return;

    try {
      await ProjectService.delete(id, user.id);
      load();
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Failed to delete project');
    }
  }

  return (
    <div className="dashboard">
      <div className="dashboard-header d-flex justify-between align-center">
        <h1>Projects</h1>
        <div className="d-flex gap-2">
          <button
            className={`btn ${showNew ? 'btn-outline' : 'btn-primary'}`}
            onClick={() => setShowNew(s => !s)}
          >
            {showNew ? 'Cancel' : 'New Project'}
          </button>
          <button className="btn btn-danger" onClick={() => { logout(); navigate('/login'); }}>
            Logout
          </button>
        </div>
      </div>

      {error && (
        <div className="alert alert-error">
          {error}
          <button className="btn-close" onClick={() => setError(null)}>×</button>
        </div>
      )}

      {showNew && (
        <ProjectForm
          onSubmit={handleCreate}
          onCancel={() => setShowNew(false)}
        />
      )}

      {loadingProjects ? (
        <div className="loading-spinner">Loading...</div>
      ) : projects.length === 0 ? (
        <div className="empty-state">
          <p>No projects yet. Create your first project!</p>
          <button className="btn btn-primary" onClick={() => setShowNew(true)}>
            Create Project
          </button>
        </div>
      ) : (
        <div className="projects-grid">
          {projects.map(project => (
            <div
              key={project.id}
              className="project-card-wrapper"
              onClick={() => navigate(`/projects/${project.id}/tasks`)}
            >
              <ProjectCard
                project={project}
                onDelete={(e) => {
                  e.stopPropagation();
                  handleDelete(project.id);
                }}
                onViewTasks={(id) => navigate(`/projects/${id}/tasks`)}
              />
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
