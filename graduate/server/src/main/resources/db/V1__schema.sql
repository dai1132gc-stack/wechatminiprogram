-- ============================================================
-- V1__schema.sql · 房屋租赁与室内环境监测系统
-- 依据技术方案 §4 + 附录 F：6 张核心表 + 2 张辅助表
-- 字符集 utf8mb4 / 引擎 InnoDB / 时间 datetime(3) / 时区 +08:00
-- 建库：CREATE DATABASE rental_env ... （见 README，脚本内不建库）
-- ============================================================

-- ============ 用户 ============
CREATE TABLE sys_user (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  openid        VARCHAR(64)  DEFAULT NULL COMMENT '微信 openid，Web 用户为 NULL',
  unionid       VARCHAR(64)  DEFAULT NULL,
  username      VARCHAR(50)  DEFAULT NULL COMMENT 'Web 登录名，小程序用户为 NULL',
  password      VARCHAR(100) DEFAULT NULL COMMENT 'BCrypt 哈希',
  phone         VARCHAR(20)  DEFAULT NULL,
  nickname      VARCHAR(50)  DEFAULT NULL,
  avatar        VARCHAR(255) DEFAULT NULL,
  real_name     VARCHAR(50)  DEFAULT NULL,
  id_card       VARCHAR(128) DEFAULT NULL COMMENT 'AES 加密存储',
  role          VARCHAR(20)  NOT NULL DEFAULT 'TENANT' COMMENT 'TENANT/LANDLORD/ADMIN',
  org_id        BIGINT       DEFAULT NULL COMMENT '所属机构',
  region_code   VARCHAR(12)  DEFAULT NULL COMMENT '行政区划码',
  status        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/DISABLED',
  token_version INT          NOT NULL DEFAULT 0 COMMENT '改密/改角色时 +1，令旧令牌立即失效',
  created_at    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  deleted       TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_openid   (openid),
  UNIQUE KEY uk_username (username),
  UNIQUE KEY uk_phone    (phone),
  KEY idx_role_status (role, status),
  KEY idx_org (org_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户';

-- ============ 房源 ============
CREATE TABLE house (
  id           BIGINT        NOT NULL AUTO_INCREMENT,
  landlord_id  BIGINT        NOT NULL,
  org_id       BIGINT        DEFAULT NULL,
  title        VARCHAR(100)  NOT NULL,
  district     VARCHAR(50)   NOT NULL,
  street       VARCHAR(50)   DEFAULT NULL,
  address      VARCHAR(200)  NOT NULL,
  lat          DECIMAL(10,7) DEFAULT NULL,
  lng          DECIMAL(10,7) DEFAULT NULL,
  area         DECIMAL(6,2)  NOT NULL,
  layout       VARCHAR(20)   NOT NULL COMMENT '如 2-1-1',
  floor        INT           DEFAULT NULL,
  total_floor  INT           DEFAULT NULL,
  orientation  VARCHAR(10)   DEFAULT NULL,
  decoration   VARCHAR(20)   DEFAULT NULL,
  rent         DECIMAL(10,2) NOT NULL,
  deposit      DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  facilities   VARCHAR(255)  DEFAULT NULL COMMENT '逗号分隔编码',
  cover_url    VARCHAR(255)  DEFAULT NULL,
  description  TEXT,
  audit_status VARCHAR(20)   NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED',
  rent_status  VARCHAR(20)   NOT NULL DEFAULT 'AVAILABLE' COMMENT 'AVAILABLE/LOCKED/RENTED/OFFLINE',
  audit_remark VARCHAR(255)  DEFAULT NULL,
  auditor_id   BIGINT        DEFAULT NULL,
  audit_time   DATETIME(3)   DEFAULT NULL,
  version      INT           NOT NULL DEFAULT 0 COMMENT '乐观锁',
  created_at   DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at   DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  deleted      TINYINT       NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_landlord (landlord_id),
  KEY idx_audit_rent (audit_status, rent_status),
  KEY idx_district_rent (district, rent_status),
  KEY idx_geo (lat, lng)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='房源';

-- ============ 房源图片（辅助表） ============
CREATE TABLE house_image (
  id         BIGINT       NOT NULL AUTO_INCREMENT,
  house_id   BIGINT       NOT NULL,
  url        VARCHAR(255) NOT NULL,
  sort       INT          NOT NULL DEFAULT 0,
  created_at DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  KEY idx_house (house_id, sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='房源图片';

-- ============ 合同（active_flag + 唯一索引兜底并发双签，附录 F 核心技巧） ============
CREATE TABLE contract (
  id                 BIGINT        NOT NULL AUTO_INCREMENT,
  contract_no        VARCHAR(32)   NOT NULL,
  house_id           BIGINT        NOT NULL,
  tenant_id          BIGINT        NOT NULL,
  landlord_id        BIGINT        NOT NULL,
  start_date         DATE          NOT NULL,
  end_date           DATE          NOT NULL,
  rent               DECIMAL(10,2) NOT NULL COMMENT '签约时快照，不随房源改价变动',
  deposit            DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  status             VARCHAR(20)   NOT NULL DEFAULT 'DRAFT'
                     COMMENT 'DRAFT/PENDING_SIGN/EFFECTIVE/EXPIRED/TERMINATED/CANCELLED',
  active_flag        TINYINT       DEFAULT NULL
                     COMMENT '生效时=1，其余=NULL。配合 uk_house_active 保证一房一约',
  sign_time          DATETIME(3)   DEFAULT NULL,
  tenant_sign_time   DATETIME(3)   DEFAULT NULL,
  landlord_sign_time DATETIME(3)   DEFAULT NULL,
  terminate_time     DATETIME(3)   DEFAULT NULL,
  terminate_reason   VARCHAR(255)  DEFAULT NULL,
  pdf_url            VARCHAR(255)  DEFAULT NULL,
  version            INT           NOT NULL DEFAULT 0,
  created_at         DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at         DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  UNIQUE KEY uk_contract_no (contract_no),
  UNIQUE KEY uk_house_active (house_id, active_flag),
  KEY idx_house_status (house_id, status),
  KEY idx_tenant_status (tenant_id, status),
  KEY idx_end_date (end_date),
  CONSTRAINT ck_contract_date CHECK (end_date > start_date),
  CONSTRAINT ck_contract_rent CHECK (rent >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='租赁合同';

-- ============ 设备 ============
CREATE TABLE device (
  id               BIGINT       NOT NULL AUTO_INCREMENT,
  device_sn        VARCHAR(64)  NOT NULL,
  house_id         BIGINT       DEFAULT NULL COMMENT '未绑定为 NULL',
  name             VARCHAR(50)  DEFAULT NULL,
  install_position VARCHAR(50)  DEFAULT NULL,
  status           VARCHAR(20)  NOT NULL DEFAULT 'UNBOUND'
                   COMMENT 'ONLINE/OFFLINE/UNBOUND/FAULT',
  sensor_profile   VARCHAR(32)  NOT NULL DEFAULT 'TVOC_CO2_MOX'
                   COMMENT '传感器配置标识，区分数据精度来源',
  last_online_at   DATETIME(3)  DEFAULT NULL,
  firmware_version VARCHAR(20)  DEFAULT NULL,
  secret_key       VARCHAR(128) DEFAULT NULL COMMENT 'HTTP 旁路上报 HMAC 密钥，加密存储',
  created_at       DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at       DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  UNIQUE KEY uk_device_sn (device_sn),
  KEY idx_house (house_id),
  KEY idx_status_online (status, last_online_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='环境监测设备';

-- ============ 环境时序记录 ============
CREATE TABLE env_record (
  id             BIGINT       NOT NULL AUTO_INCREMENT,
  device_id      BIGINT       NOT NULL,
  device_sn      VARCHAR(64)  NOT NULL COMMENT '冗余，便于按设备查',
  house_id       BIGINT       DEFAULT NULL COMMENT '冗余，查询不必回溯',
  contract_id    BIGINT       DEFAULT NULL COMMENT '冗余，标记数据归属的租期',
  temperature    DECIMAL(5,2) DEFAULT NULL COMMENT '℃',
  humidity       DECIMAL(5,2) DEFAULT NULL COMMENT '%RH',
  hcho           DECIMAL(6,3) DEFAULT NULL COMMENT 'mg/m³（TVOC 反算估算）',
  tvoc           DECIMAL(6,3) DEFAULT NULL COMMENT 'mg/m³',
  co2            INT          DEFAULT NULL COMMENT 'ppm（估算值）',
  pm25           INT          DEFAULT NULL COMMENT 'μg/m³',
  quality_flag   TINYINT      NOT NULL DEFAULT 0
                 COMMENT '0正常/1补传/2越界/3跳变/4故障/5陈旧/6预热',
  sensor_profile VARCHAR(32)  NOT NULL DEFAULT 'TVOC_CO2_MOX',
  sample_count   INT          NOT NULL DEFAULT 0 COMMENT '本聚合周期原始样本数',
  collect_time   DATETIME(3)  NOT NULL COMMENT '设备侧采集时间',
  receive_time   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  raw_payload    JSON         DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_device_collect (device_sn, collect_time) COMMENT '幂等去重',
  KEY idx_house_time (house_id, collect_time DESC),
  KEY idx_contract (contract_id),
  KEY idx_quality (quality_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='环境时序记录';

-- ============ 超标告警 ============
CREATE TABLE env_alarm (
  id            BIGINT        NOT NULL AUTO_INCREMENT,
  house_id      BIGINT        NOT NULL,
  device_id     BIGINT        NOT NULL,
  contract_id   BIGINT        DEFAULT NULL,
  metric        VARCHAR(20)   NOT NULL COMMENT 'HCHO/TVOC/CO2/TEMPERATURE/HUMIDITY',
  value         DECIMAL(10,3) NOT NULL COMMENT '触发时的值',
  threshold     DECIMAL(10,3) NOT NULL,
  level         VARCHAR(10)   NOT NULL COMMENT 'WARN/DANGER',
  status        VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE'
                COMMENT 'ACTIVE/RECOVERED/HANDLED/IGNORED',
  triggered_at  DATETIME(3)   NOT NULL,
  recovered_at  DATETIME(3)   DEFAULT NULL,
  handled_by    BIGINT        DEFAULT NULL,
  handle_remark VARCHAR(255)  DEFAULT NULL,
  notified      TINYINT       NOT NULL DEFAULT 0 COMMENT '是否已推送订阅消息',
  created_at    DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  KEY idx_house_status_time (house_id, status, triggered_at DESC),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='超标告警';

-- ============ 操作日志（辅助表） ============
CREATE TABLE sys_operation_log (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  trace_id    VARCHAR(32)  DEFAULT NULL,
  user_id     BIGINT       DEFAULT NULL,
  username    VARCHAR(50)  DEFAULT NULL,
  role        VARCHAR(20)  DEFAULT NULL,
  module      VARCHAR(50)  DEFAULT NULL,
  action      VARCHAR(50)  DEFAULT NULL,
  method      VARCHAR(10)  DEFAULT NULL,
  uri         VARCHAR(255) DEFAULT NULL,
  params      TEXT COMMENT '已脱敏的入参 JSON',
  result_code INT          DEFAULT NULL,
  ip          VARCHAR(50)  DEFAULT NULL,
  user_agent  VARCHAR(255) DEFAULT NULL,
  cost_ms     INT          DEFAULT NULL,
  success     TINYINT      NOT NULL DEFAULT 1,
  created_at  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  KEY idx_user_time (user_id, created_at),
  KEY idx_trace (trace_id),
  KEY idx_module_time (module, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志';
