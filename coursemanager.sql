-- =============================================
-- 图书信息管理系统数据库初始化脚本
-- 数据库名: coursemanager
-- =============================================

-- 如果数据库存在则删除
DROP DATABASE IF EXISTS coursemanager;

-- 创建数据库
CREATE DATABASE coursemanager DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用数据库
USE coursemanager;

-- =============================================
-- 创建表结构
-- =============================================

-- 用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号码',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱地址',
    role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin管理员，user普通用户',
    status INT NOT NULL DEFAULT 1 COMMENT '状态：0禁用，1启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 图书分类表
CREATE TABLE book_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    description VARCHAR(200) DEFAULT NULL COMMENT '分类描述',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

-- 图书表
CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    book_name VARCHAR(100) NOT NULL COMMENT '图书名称',
    author VARCHAR(50) NOT NULL COMMENT '作者',
    publisher VARCHAR(100) DEFAULT NULL COMMENT '出版社',
    isbn VARCHAR(50) DEFAULT NULL COMMENT 'ISBN编号',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    total_count INT NOT NULL DEFAULT 0 COMMENT '馆藏总数',
    available_count INT NOT NULL DEFAULT 0 COMMENT '可借数量',
    price DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格',
    publish_date DATE DEFAULT NULL COMMENT '出版日期',
    cover_image VARCHAR(255) DEFAULT NULL COMMENT '封面图片路径',
    description TEXT DEFAULT NULL COMMENT '图书简介',
    status INT NOT NULL DEFAULT 1 COMMENT '状态：0下架，1上架',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (category_id) REFERENCES book_categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- 借阅记录表
CREATE TABLE borrow_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    borrow_date DATETIME NOT NULL COMMENT '借阅日期',
    due_date DATETIME NOT NULL COMMENT '应还日期',
    return_date DATETIME DEFAULT NULL COMMENT '实际归还日期',
    renew_count INT NOT NULL DEFAULT 0 COMMENT '续借次数',
    overdue_days INT NOT NULL DEFAULT 0 COMMENT '逾期天数',
    status VARCHAR(20) NOT NULL DEFAULT 'borrowing' COMMENT '状态：borrowing借阅中，returned已归还，overdue已逾期',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

-- 预约记录表
CREATE TABLE reservation_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    reservation_date DATETIME NOT NULL COMMENT '预约日期',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending待审核，approved已通过，cancelled已取消，completed已完成',
    remark VARCHAR(200) DEFAULT NULL COMMENT '备注信息',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约记录表';

-- 公告表
CREATE TABLE announcements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(200) NOT NULL COMMENT '公告标题',
    content TEXT NOT NULL COMMENT '公告内容',
    is_top INT NOT NULL DEFAULT 0 COMMENT '是否置顶：0否，1是',
    publish_user_id BIGINT NOT NULL COMMENT '发布人ID',
    status INT NOT NULL DEFAULT 1 COMMENT '状态：0草稿，1已发布',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (publish_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- =============================================
-- 插入初始数据
-- =============================================

-- 插入用户数据（密码使用MD5加密，123456的MD5值为e10adc3949ba59abbe56e057f20f883e）
INSERT INTO users (username, password, real_name, phone, email, role, status) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', '13800138000', 'admin@library.com', 'admin', 1),
('zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', '13800138001', 'zhangsan@example.com', 'user', 1),
('lisi', 'e10adc3949ba59abbe56e057f20f883e', '李四', '13800138002', 'lisi@example.com', 'user', 1),
('wangwu', 'e10adc3949ba59abbe56e057f20f883e', '王五', '13800138003', 'wangwu@example.com', 'user', 1),
('zhaoliu', 'e10adc3949ba59abbe56e057f20f883e', '赵六', '13800138004', 'zhaoliu@example.com', 'user', 1),
('sunqi', 'e10adc3949ba59abbe56e057f20f883e', '孙七', '13800138005', 'sunqi@example.com', 'user', 1),
('zhouba', 'e10adc3949ba59abbe56e057f20f883e', '周八', '13800138006', 'zhouba@example.com', 'user', 0);

-- 插入图书分类数据
INSERT INTO book_categories (category_name, description, sort_order) VALUES
('计算机技术', '计算机、编程、网络等相关技术书籍', 1),
('文学艺术', '小说、诗歌、散文、艺术类书籍', 2),
('历史传记', '历史、人物传记类书籍', 3),
('经济管理', '经济学、管理学、商业类书籍', 4),
('自然科学', '数学、物理、化学、生物等自然科学类书籍', 5),
('社会科学', '哲学、社会学、心理学等社会科学类书籍', 6),
('生活健康', '养生、健康、美食、旅游类书籍', 7);

-- 插入图书数据
INSERT INTO books (book_name, author, publisher, isbn, category_id, total_count, available_count, price, publish_date, description, status) VALUES
('Java核心技术', 'Cay S. Horstmann', '机械工业出版社', '9787111544968', 1, 10, 8, 119.00, '2018-09-01', 'Java领域最有影响力的经典著作之一，全面覆盖Java核心技术，深入浅出讲解。', 1),
('Python编程从入门到实践', 'Eric Matthes', '人民邮电出版社', '9787115428028', 1, 8, 5, 89.00, '2016-07-01', '一本针对所有层次的Python读者而作的Python入门书，实践性强。', 1),
('深入理解计算机系统', 'Randal E. Bryant', '机械工业出版社', '9787111544937', 1, 5, 3, 139.00, '2016-11-01', '从程序员的视角深入理解计算机系统，经典计算机著作。', 1),
('算法导论', 'Thomas H. Cormen', '机械工业出版社', '9787111407010', 1, 6, 4, 128.00, '2012-12-01', '算法领域的经典之作，MIT计算机教材，内容全面深入。', 1),
('三体', '刘慈欣', '重庆出版社', '9787536692930', 2, 15, 10, 23.00, '2008-01-01', '中国科幻文学的里程碑之作，雨果奖获奖作品，史诗级科幻巨著。', 1),
('平凡的世界', '路遥', '北京十月文艺出版社', '9787530216781', 2, 12, 8, 99.00, '2012-03-01', '茅盾文学奖作品，展现普通人在大时代历史进程中所走过的艰难曲折道路。', 1),
('活着', '余华', '作家出版社', '9787506365437', 2, 10, 7, 20.00, '2012-08-01', '当代文学经典之作，讲述一个人一生的故事，生命的坚韧与美好。', 1),
('百年孤独', '加西亚·马尔克斯', '南海出版公司', '9787544253994', 2, 8, 5, 39.50, '2011-06-01', '魔幻现实主义文学代表作，诺贝尔文学奖获奖作品。', 1),
('人类简史', '尤瓦尔·赫拉利', '中信出版社', '9787508647357', 3, 10, 6, 68.00, '2014-11-01', '从十万年前到21世纪，人类演化史的宏大叙事。', 1),
('明朝那些事儿', '当年明月', '浙江人民出版社', '9787213035463', 3, 9, 6, 29.80, '2006-09-01', '用现代语言讲述明朝历史，幽默风趣，深受读者喜爱。', 1);

-- 插入借阅记录数据
INSERT INTO borrow_records (user_id, book_id, borrow_date, due_date, return_date, renew_count, overdue_days, status) VALUES
(2, 1, '2025-10-01 10:00:00', '2025-10-31 23:59:59', '2025-10-25 14:30:00', 0, 0, 'returned'),
(3, 2, '2025-10-05 09:30:00', '2025-11-04 23:59:59', NULL, 1, 0, 'borrowing'),
(4, 5, '2025-10-08 14:20:00', '2025-11-07 23:59:59', NULL, 0, 0, 'borrowing'),
(5, 6, '2025-09-15 11:00:00', '2025-10-15 23:59:59', NULL, 0, 7, 'overdue'),
(2, 3, '2025-10-12 16:45:00', '2025-11-11 23:59:59', NULL, 0, 0, 'borrowing'),
(3, 7, '2025-10-10 10:15:00', '2025-11-09 23:59:59', '2025-10-20 09:00:00', 0, 0, 'returned'),
(6, 8, '2025-10-15 13:30:00', '2025-11-14 23:59:59', NULL, 0, 0, 'borrowing'),
(4, 9, '2025-10-18 15:00:00', '2025-11-17 23:59:59', NULL, 0, 0, 'borrowing');

-- 插入预约记录数据
INSERT INTO reservation_records (user_id, book_id, reservation_date, status, remark) VALUES
(5, 1, '2025-10-20 10:30:00', 'approved', '希望尽快能借到这本书'),
(6, 2, '2025-10-21 14:20:00', 'pending', '急需学习Python'),
(2, 4, '2025-10-19 09:15:00', 'approved', '课程需要用到'),
(3, 9, '2025-10-22 11:00:00', 'pending', '很想读这本书'),
(4, 5, '2025-10-18 16:30:00', 'cancelled', '已通过其他渠道获得'),
(5, 10, '2025-10-17 10:00:00', 'completed', '已完成借阅');

-- 插入公告数据
INSERT INTO announcements (title, content, is_top, publish_user_id, status) VALUES
('图书馆系统上线公告', '尊敬的读者：我馆图书管理系统已正式上线，欢迎大家使用在线借阅功能。系统提供图书检索、在线借阅、预约等服务，为读者提供更便捷的服务体验。', 1, 1, 1),
('国庆节期间开馆时间调整通知', '各位读者：根据国庆节假期安排，图书馆开馆时间调整如下：10月1日-3日闭馆，10月4日-7日开馆时间为9:00-17:00，10月8日起恢复正常开馆时间。请大家合理安排借阅时间。', 0, 1, 1),
('新书推荐：计算机类图书上架', '本月新增计算机类图书50余册，涵盖Java、Python、数据库、人工智能等热门领域，欢迎广大读者借阅。详情请登录系统查看图书列表。', 0, 1, 1),
('关于图书借阅期限的说明', '为了提高图书流通效率，保障更多读者的借阅权益，图书借阅期限统一为30天，可续借一次，续借期限为15天。逾期未还将影响后续借阅，请大家按时归还。', 0, 1, 1),
('图书馆读书分享活动通知', '为营造良好的读书氛围，图书馆将于本月28日下午2点在三楼会议室举办读书分享会，欢迎喜欢阅读的朋友参加。主题：经典文学作品赏析。', 0, 1, 1),
('系统维护通知', '因系统升级维护，本周六（10月26日）00:00-06:00系统将暂停服务，期间无法进行在线借阅、续借等操作，请大家提前安排。给您带来的不便敬请谅解。', 0, 1, 1);

-- =============================================
-- 创建索引优化查询性能
-- =============================================

-- 用户表索引
CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_role ON users(role);

-- 图书表索引
CREATE INDEX idx_book_name ON books(book_name);
CREATE INDEX idx_author ON books(author);
CREATE INDEX idx_isbn ON books(isbn);
CREATE INDEX idx_category_id ON books(category_id);

-- 借阅记录表索引
CREATE INDEX idx_user_id ON borrow_records(user_id);
CREATE INDEX idx_book_id ON borrow_records(book_id);
CREATE INDEX idx_status ON borrow_records(status);

-- 预约记录表索引
CREATE INDEX idx_reservation_user_id ON reservation_records(user_id);
CREATE INDEX idx_reservation_book_id ON reservation_records(book_id);

-- 公告表索引
CREATE INDEX idx_is_top ON announcements(is_top);
CREATE INDEX idx_status ON announcements(status);

-- =============================================
-- 初始化完成
-- =============================================
