INSERT INTO users (first_name, last_name, email, phone, active, deleted, version, created_at, updated_at, created_by, updated_by)
VALUES
('Omar', 'Garcia', 'omar@example.com', '8112345678', TRUE, FALSE, 0, NOW(), NOW(), 'seed', 'seed'),
('Ana', 'Lopez', 'ana@example.com', NULL, TRUE, FALSE, 0, NOW(), NOW(), 'seed', 'seed');
