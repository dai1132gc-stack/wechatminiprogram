-- ============================================================
-- V3__seed_demo.sql · 演示数据（与 Web/小程序 Mock 账号一致）
-- 密码哈希为 BCrypt($2a$)：admin123 / 123456
-- ============================================================

-- 演示账号
INSERT INTO sys_user (id, username, password, nickname, real_name, phone, role, status) VALUES
(1, 'admin',     '$2a$10$4TgtFg287EGaJaGb5i60MueCqlQf4PHY2p1sSilFYgaNqB9Rw0WQS', '张管理', '张管理', '13800000001', 'ADMIN',    'ACTIVE'),
(2, 'landlord1', '$2a$10$zjPtsYQ5Hi9TEba6UFtTk../dMRZtv6xubyNrLwYbGnGShiNKfsKa', '李房东', '李明',   '13800000002', 'LANDLORD', 'ACTIVE'),
(10086, NULL, NULL, '微信用户_8a2f', NULL, NULL, 'TENANT', 'ACTIVE');

-- 让小程序 Mock 用户的 openid 落库（与 wechat/mock/data/user.js 一致）
UPDATE sys_user SET openid = 'mock-openid-10086' WHERE id = 10086;

-- 演示房源（房东 2 名下 6 套，状态覆盖各分支）
INSERT INTO house (id, landlord_id, title, district, street, address, area, layout, rent, deposit, audit_status, rent_status, description) VALUES
(301, 2, '阳光花园 3 栋 301', '西湖区', '文一西路', '文一西路 88 号', 89.50, '2-1-1', 3200.00, 3200.00, 'APPROVED',  'AVAILABLE', '南北通透，近地铁 2 号线，拎包入住'),
(302, 2, '翠苑小区 5 栋 202', '西湖区', '文二路',   '文二路 138 号',  65.00, '1-1-1', 2600.00, 2600.00, 'APPROVED',  'RENTED',    '精装一居室，已签约'),
(303, 2, '文鼎苑 2 栋 803',   '余杭区', '文一西路', '文一西路 969 号', 120.00, '3-2-2', 5600.00, 5600.00, 'PENDING',   'AVAILABLE', '新上三居室，等待审核'),
(304, 2, '钱塘府 7 栋 1101',  '滨江区', '江南大道', '江南大道 588 号', 98.00, '2-2-1', 4800.00, 4800.00, 'REJECTED',  'AVAILABLE', '图片不清晰被驳回，待修改重新提交'),
(305, 2, '桃源居 1 栋 502',   '拱墅区', '莫干山路', '莫干山路 760 号', 72.00, '2-1-1', 3400.00, 3400.00, 'APPROVED',  'LOCKED',    '已有租客申请锁定中'),
(306, 2, '阳光花园 8 栋 132', '西湖区', '文一西路', '文一西路 88 号',  54.30, '1-1-1', 2400.00, 2400.00, 'APPROVED',  'AVAILABLE', '小户型，环境监测设备已装');

UPDATE house SET audit_remark = '图片模糊，请重新上传后提交', auditor_id = 1, audit_time = NOW(3) WHERE id = 304;

-- 演示设备：301 与 306 已绑定在线，302 离线
INSERT INTO device (id, device_sn, house_id, name, install_position, status, sensor_profile, last_online_at, firmware_version) VALUES
(12, 'ENV-SN-0012', 301, '客厅监测点', '客厅电视柜', 'ONLINE',  'TVOC_CO2_MOX', NOW(3), 'v0.9.1'),
(13, 'ENV-SN-0013', 306, '主卧监测点', '主卧床头',   'ONLINE',  'TVOC_CO2_MOX', NOW(3), 'v0.9.1'),
(14, 'ENV-SN-0014', 302, '客厅监测点', '客厅',       'OFFLINE', 'TVOC_CO2_MOX', DATE_SUB(NOW(3), INTERVAL 2 HOUR), 'v0.8.7'),
(15, 'ENV-SN-0015', NULL, NULL, NULL,  'UNBOUND', 'TVOC_CO2_MOX', NULL, NULL);

-- 演示合同：302 在租（active_flag=1，唯一索引生效中）
INSERT INTO contract (id, contract_no, house_id, tenant_id, landlord_id, start_date, end_date, rent, deposit, status, active_flag, tenant_sign_time, landlord_sign_time, sign_time) VALUES
(501, 'HT202609010001', 302, 10086, 2, '2026-09-01', '2027-08-31', 2600.00, 2600.00, 'EFFECTIVE', 1, NOW(3), NOW(3), NOW(3));

-- 演示环境读数：301 正常、306 超标（hcho > 0.08，供列表红色分支演示）
INSERT INTO env_record (device_id, device_sn, house_id, contract_id, temperature, humidity, hcho, tvoc, co2, quality_flag, sample_count, collect_time)
VALUES
(12,'ENV-SN-0012',301,501,26.80,58.30,0.032,0.420,780,0,60,NOW(3)),
(13,'ENV-SN-0013',306,NULL,24.60,61.20,0.095,0.510,860,0,60,NOW(3));
