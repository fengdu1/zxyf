CREATE TABLE dept (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '部门ID(唯一标识，自增主键)',
    name VARCHAR(10) NOT NULL UNIQUE COMMENT '部门名称',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后操作时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门信息表';

CREATE TABLE emp (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '员工ID',
    username VARCHAR(20) NOT NULL UNIQUE COMMENT '用户名(2-20位)',
    password VARCHAR(64) DEFAULT '123456' COMMENT '登录密码',
    name VARCHAR(10) NOT NULL COMMENT '姓名(2-10位)',
    gender TINYINT COMMENT '性别，1：男，2：女',
    phone CHAR(11) NOT NULL UNIQUE COMMENT '手机号(11位)',
    position TINYINT COMMENT '职位，1：班主任，2：讲师，3：学工主管，4：教研主管，5：咨询师',
    salary INT COMMENT '薪资(整数存储)',
    image VARCHAR(255) COMMENT '头像路径',
    original_name VARCHAR(255) COMMENT '源文件名称',
    hire_date DATE COMMENT '入职日期',
    dept_id INT COMMENT '所属部门ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后操作时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工信息表';

CREATE TABLE emp_expr (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '经历ID',
    emp_id INT COMMENT '关联员工ID',
    company VARCHAR(50) COMMENT '公司名称',
    position VARCHAR(50) COMMENT '担任职位',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工工作经历表';

INSERT INTO dept (name) VALUES
('学工部'),
('教研部'),
('就业部'),
('教务部'),
('产品部'),
('研发中心'),
('质量保障部'),
('市场拓展部'),
('客户成功部'),
('人力资源部'),
('财务审计部'),
('法务合规部'),
('行政后勤部'),
('品牌运营部'),
('战略投资部');

INSERT INTO emp (username, name, gender, phone, position, salary, dept_id, hire_date) VALUES
('zhaomin', '赵敏', 2, '13810102001', 1, 4800, 1, '2020-03-15'),
('yangxiao', '杨逍', 1, '13921213002', 2, 6500, 2, '2019-07-22'),
('weiyixiao', '韦一笑', 1, '13632324003', 3, 8200, 3, '2021-11-01'),
('zhangwuji', '张无忌', 1, '15043435004', 4, 9500, 4, '2022-02-14'),
('zhouzhiruo', '周芷若', 2, '15156567005', 5, 7800, 5, '2020-08-30'),
('xiaozhao', '小昭', 2, '15267678006', 1, 5200, 6, '2023-01-05'),
('yinliting', '殷梨亭', 1, '15378789007', 2, 6800, 7, '2018-05-17'),
('songyuanqiao', '宋远桥', 1, '15489890008', 3, 8700, 8, '2017-09-10'),
('yulianzhou', '俞莲舟', 1, '15590901009', 4, 9900, 9, '2021-04-21'),
('daiyisi', '黛绮丝', 2, '15601011010', 5, 7300, 10, '2019-12-08'),
('xiexun', '谢逊', 1, '15712122011', 4, 5500, 11, '2022-07-19'),
('yinyesu', '殷野王', 1, '15823233012', 2, 7100, 12, '2020-06-25'),
('yangdingtian', '阳顶天', 1, '15934344013', 3, 8900, 13, '2016-08-14'),
('fanyao', '范遥', 1, '15045455014', 4, 10200, 14, '2015-11-30'),
('jixiaofu', '纪晓芙', 2, '15156566015', 5, 7600, 15, '2023-03-07');

-- 工作经历表测试数据（时间线逻辑验证）
INSERT INTO emp_expr (emp_id, company, position, start_date, end_date) VALUES
(1, '光明顶科技', '学员管理专员', '2018-04-01', '2020-02-28'),
(2, '武当山网络', '课程研发工程师', '2017-01-15', '2019-06-30'),
(3, '峨眉云计算', '就业指导顾问', '2019-09-01', '2021-10-31'),
(4, '明教教育科技', '教务系统架构师', '2020-12-01', '2022-01-15'),
(5, '昆仑数据', '产品经理', '2018-11-01', '2020-07-31'),
(6, '波斯国际集团', '行政助理', '2021-03-01', '2022-12-31'),
(7, '少林科技学院', '质量保障专家', '2016-05-01', '2018-04-30'),
(8, '华山网络科技', '市场总监', '2015-02-01', '2017-08-31'),
(9, '崆峒人工智能', '客户成功经理', '2019-01-15', '2021-03-15'),
(10, '星宿派科技', 'HRBP', '2017-07-01', '2019-11-30'),
(11, '逍遥派金融', '财务分析师', '2020-09-01', '2022-05-31'),
(12, '丐帮物流科技', '法务顾问', '2018-03-01', '2020-04-30'),
(13, '古墓派科技', '品牌运营总监', '2014-06-01', '2016-07-31'),
(14, '全真教投资', '战略投资经理', '2013-08-01', '2015-10-31'),
(15, '桃花岛研究院', '教研主管', '2021-05-01', '2023-02-28');
