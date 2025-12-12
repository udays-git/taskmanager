export const formatDateTime = (dateTimeString) => {
  if (!dateTimeString) return 'Unknown';
  
  try {
    const date = new Date(dateTimeString);
    return date.toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      hour12: true
    });
  } catch (error) {
    console.error('Error formatting date:', error);
    return 'Invalid date';
  }
};

export const formatDateLong = (dateString) => {
  if (!dateString) return 'No due date';
  
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  } catch (error) {
    console.error('Error formatting date:', error);
    return 'Invalid date';
  }
};

export const isOverdue = (dueDate, status) => {
  if (!dueDate || status === 'completed') return false;
  
  try {
    const due = new Date(dueDate);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return due < today;
  } catch (error) {
    return false;
  }
};

export const getPriorityLevelText = (priority) => {
  const numPriority = priority || 5;
  if (numPriority >= 9) return 'Very High';
  if (numPriority >= 7) return 'High';
  if (numPriority >= 5) return 'Medium';
  if (numPriority >= 3) return 'Low';
  return 'Very Low';
};

export const getStatusColor = (status) => {
  switch (status?.toLowerCase()) {
    case 'completed': return '#10b981';
    case 'in-progress': return '#3b82f6';
    case 'pending': return '#f59e0b';
    default: return '#64748b';
  }
};