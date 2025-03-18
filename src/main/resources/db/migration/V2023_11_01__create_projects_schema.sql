-- Create a separate schema for the projects domain
CREATE SCHEMA IF NOT EXISTS projects;

-- Create projects table
CREATE TABLE projects.projects (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    owner_id VARCHAR(36) NOT NULL,
    team_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL,
    completion_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create indexes for common lookup patterns
CREATE INDEX idx_projects_owner_id ON projects.projects(owner_id);
CREATE INDEX idx_projects_team_id ON projects.projects(team_id);
CREATE INDEX idx_projects_status ON projects.projects(status);
