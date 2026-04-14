INSERT INTO users (username, email, password, role, status, created_at, updated_at)
VALUES (
  'admin',
  'admin@company.com',
  '$2a$10$e74w46CKgHrrGIqzycQDM.3dcLzkwfstcs23s30Sj5R5LjVv8E7z6',
  'ADMIN',
  'APPROVED',
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
);