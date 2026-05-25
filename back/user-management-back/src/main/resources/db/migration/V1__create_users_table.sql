CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(80) NOT NULL,
  last_name VARCHAR(80) NOT NULL,
  email VARCHAR(160) NOT NULL,
  phone VARCHAR(30),
  active BOOLEAN NOT NULL DEFAULT TRUE,

  deleted BOOLEAN NOT NULL DEFAULT FALSE,
  deleted_at DATETIME NULL,
  deleted_by VARCHAR(80) NULL,

  version BIGINT NOT NULL DEFAULT 0,

  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  created_by VARCHAR(80) NOT NULL,
  updated_by VARCHAR(80) NOT NULL,

  CONSTRAINT uk_users_email UNIQUE (email),
  INDEX idx_users_active (active),
  INDEX idx_users_created_at (created_at)
);
