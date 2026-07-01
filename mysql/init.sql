CREATE DATABASE IF NOT EXISTS bug_management
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE bug_management;

DROP TABLE IF EXISTS bug_operation_logs;
DROP TABLE IF EXISTS bugs;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  nickname VARCHAR(50),
  role VARCHAR(30) NOT NULL,
  created_at DATETIME,
  updated_at DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE bugs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  module_name VARCHAR(100),
  severity VARCHAR(30),
  description TEXT,
  reproduce_steps TEXT,
  screenshot_url VARCHAR(500),
  status VARCHAR(30),
  assignee_id BIGINT,
  creator_id BIGINT,
  created_at DATETIME,
  updated_at DATETIME,
  INDEX idx_bugs_title (title),
  INDEX idx_bugs_status (status),
  INDEX idx_bugs_severity (severity),
  INDEX idx_bugs_assignee_id (assignee_id),
  INDEX idx_bugs_creator_id (creator_id),
  INDEX idx_bugs_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE bug_operation_logs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  bug_id BIGINT NOT NULL,
  operator_id BIGINT,
  operation_type VARCHAR(50),
  old_status VARCHAR(30),
  new_status VARCHAR(30),
  old_assignee_id BIGINT,
  new_assignee_id BIGINT,
  remark VARCHAR(500),
  created_at DATETIME,
  INDEX idx_logs_bug_id (bug_id),
  INDEX idx_logs_operator_id (operator_id),
  INDEX idx_logs_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO users (id, username, password, nickname, role, created_at, updated_at) VALUES
  (1, 'admin', '123456', '管理员', 'ADMIN', NOW(), NOW()),
  (2, 'tester', '123456', '测试人员', 'TESTER', NOW(), NOW()),
  (3, 'developer', '123456', '开发人员', 'DEVELOPER', NOW(), NOW());

INSERT INTO bugs (
  id,
  title,
  module_name,
  severity,
  description,
  reproduce_steps,
  screenshot_url,
  status,
  assignee_id,
  creator_id,
  created_at,
  updated_at
) VALUES
  (
    1,
    '登录页面密码错误提示不明显',
    '用户登录',
    '中',
    '用户输入错误密码后，页面只清空表单，没有给出明确错误提示。',
    '1. 打开登录页；2. 输入 admin 和错误密码；3. 点击登录；4. 观察页面反馈。',
    'login-error.png',
    '已提交',
    3,
    2,
    NOW(),
    NOW()
  ),
  (
    2,
    'Bug 列表按状态筛选后分页数量异常',
    '缺陷管理',
    '高',
    '选择状态筛选条件后，列表数据变少，但分页总数仍显示筛选前数量。',
    '1. 登录系统；2. 进入 Bug 列表；3. 选择状态为已提交；4. 查看分页总数。',
    'bug-list-filter.png',
    '已修复',
    3,
    2,
    DATE_SUB(NOW(), INTERVAL 1 DAY),
    NOW()
  ),
  (
    3,
    '统计页面严重 Bug 数量展示错误',
    '统计分析',
    '严重',
    '控制台严重 Bug 数量与列表中严重等级 Bug 数量不一致。',
    '1. 登录管理员账号；2. 进入控制台；3. 对比 Bug 列表严重等级数量。',
    'statistics-serious-count.png',
    '已关闭',
    3,
    1,
    DATE_SUB(NOW(), INTERVAL 2 DAY),
    NOW()
  );

INSERT INTO bug_operation_logs (
  bug_id,
  operator_id,
  operation_type,
  old_status,
  new_status,
  old_assignee_id,
  new_assignee_id,
  remark,
  created_at
) VALUES
  (1, 2, '新增 Bug', NULL, '已提交', NULL, 3, '测试人员提交登录提示问题，并指派给开发人员。', NOW()),
  (2, 2, '新增 Bug', NULL, '已提交', NULL, 3, '测试人员提交列表分页问题。', DATE_SUB(NOW(), INTERVAL 1 DAY)),
  (2, 3, '修改状态', '已提交', '已修复', 3, 3, '开发人员已修复分页总数计算逻辑。', NOW()),
  (3, 1, '新增 Bug', NULL, '已提交', NULL, 3, '管理员录入统计页面问题。', DATE_SUB(NOW(), INTERVAL 2 DAY)),
  (3, 3, '修改状态', '已提交', '已修复', 3, 3, '开发人员已修复统计查询条件。', DATE_SUB(NOW(), INTERVAL 1 DAY)),
  (3, 2, '关闭 Bug', '已修复', '已关闭', 3, 3, '测试人员复测通过，关闭缺陷。', NOW());
