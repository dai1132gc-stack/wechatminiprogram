-- ============================================================
-- V2__seed_dict.sql · 字典表 + 初始字典（GET /dict/{type} 数据源）
-- ============================================================

CREATE TABLE sys_dict (
  id         BIGINT      NOT NULL AUTO_INCREMENT,
  type       VARCHAR(32) NOT NULL COMMENT 'LAYOUT/ORIENTATION/DECORATION/FACILITY/DISTRICT',
  label      VARCHAR(50) NOT NULL,
  value      VARCHAR(50) NOT NULL,
  sort       INT         NOT NULL DEFAULT 0,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  KEY idx_type_sort (type, sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典';

INSERT INTO sys_dict (type, label, value, sort) VALUES
('LAYOUT',      '1-1-1', '1-1-1', 1),
('LAYOUT',      '2-1-1', '2-1-1', 2),
('LAYOUT',      '2-2-1', '2-2-1', 3),
('LAYOUT',      '3-2-2', '3-2-2', 4),
('ORIENTATION', '南',     'S', 1),
('ORIENTATION', '南北',   'SN', 2),
('ORIENTATION', '东南',   'SE', 3),
('ORIENTATION', '东',     'E',  4),
('DECORATION',  '毛坯',   'ROUGH',    1),
('DECORATION',  '简装',   'SIMPLE',   2),
('DECORATION',  '精装',   'DELUXE',   3),
('FACILITY',    '空调',   'AC',       1),
('FACILITY',    '洗衣机', 'WASHER',   2),
('FACILITY',    '冰箱',   'FRIDGE',   3),
('FACILITY',    '热水器', 'HEATER',   4),
('FACILITY',    '宽带',   'BROADBAND',5),
('FACILITY',    '独立卫浴','BATH',    6);
