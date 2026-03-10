-- ─── FocusFlow Database Setup ────────────────────────────────────────────────

CREATE DATABASE IF NOT EXISTS focusflow_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'focusflow_user'@'localhost' IDENTIFIED BY 'your_password_here';
GRANT ALL PRIVILEGES ON focusflow_db.* TO 'focusflow_user'@'localhost';
FLUSH PRIVILEGES;

USE focusflow_db;

-- ─── Tasks Table ──────────────────────────────────────────────────────────────
-- Hibernate auto-creates this via ddl-auto=update, but here for reference:

CREATE TABLE IF NOT EXISTS tasks (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    title               VARCHAR(255)    NOT NULL,
    description         VARCHAR(1000),
    status              ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED', 'ARCHIVED')
                                        NOT NULL DEFAULT 'PENDING',
    priority            ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT')
                                        NOT NULL DEFAULT 'MEDIUM',
    pomodoro_count      INT             NOT NULL DEFAULT 0,
    estimated_pomodoros INT             NOT NULL DEFAULT 1,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    completed_at        DATETIME,
    PRIMARY KEY (id),
    INDEX idx_status    (status),
    INDEX idx_priority  (priority),
    INDEX idx_completed (completed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── Sample Data ──────────────────────────────────────────────────────────────
INSERT INTO tasks (title, description, priority, estimated_pomodoros) VALUES
    ('Set up Spring Boot project',  'Initialize Maven project with dependencies', 'HIGH',   2),
    ('Design MySQL schema',         'Create tables for tasks and sessions',        'HIGH',   1),
    ('Build REST API',              'Implement CRUD endpoints for tasks',           'URGENT', 4),
    ('Connect frontend to API',     'Replace localStorage calls with fetch()',      'MEDIUM', 3),
    ('Add weekly chart',            'Visualize productivity data from API',         'LOW',    2);