import React from 'react';
import { formatDateLong } from '../../utils/helpers';
import '../../styles/app.css';

function ProjectCard({ project, onDelete, onViewTasks }) {
  return (
    <div className="task-card">
      <div className="task-header">
        <h3 className="task-title">{project.title}</h3>
      </div>

      <div className="task-details">
        <p className="task-description">
          {project.description || 'No description'}
        </p>
      </div>

      <div className="project-meta d-flex gap-2 mt-2">
        {project.startDate && (
          <span className="meta-item">
            Start: {formatDateLong(project.startDate)}
          </span>
        )}
        {project.endDate && (
          <span className="meta-item">
            End: {formatDateLong(project.endDate)}
          </span>
        )}
      </div>

      <div className="project-actions mt-3">
        <button
          className="btn btn-primary btn-sm"
          onClick={(e) => {
            e.stopPropagation();
            if (typeof onViewTasks === 'function') onViewTasks(project.id);
          }}
        >
          View Tasks
        </button>

        <button
          className="btn btn-outline btn-sm"
          onClick={(e) => {
            e.stopPropagation();
            if (typeof onDelete === 'function') onDelete(e);
          }}
        >
          Delete
        </button>
      </div>
    </div>
  );
}

export default ProjectCard;
