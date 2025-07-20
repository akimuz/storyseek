-- 切换到目标数据库
USE `dev_storyseek_01`;
-- 创建 book 表
CREATE TABLE book (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  title VARCHAR(128) NOT NULL COMMENT '书名',
  type VARCHAR(10) DEFAULT NULL COMMENT '书籍类型',
  description TEXT DEFAULT NULL COMMENT '书籍简介',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书籍表';

-- 创建 book_volume_chapter 表
CREATE TABLE book_volume_chapter (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  book_id BIGINT NOT NULL COMMENT '所属书籍ID',
  parent_id BIGINT DEFAULT 0 COMMENT '父节点ID，0表示顶层卷',
  name VARCHAR(255) NOT NULL COMMENT '卷或章节名称',
  type TINYINT NOT NULL COMMENT '类型，1=卷，2=章',
  order_num INT DEFAULT 0 COMMENT '排序字段',
  content TEXT DEFAULT NULL COMMENT '章节正文'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书籍卷章表';

-- 创建 role 表
CREATE TABLE role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  book_id BIGINT NOT NULL COMMENT '关联的书籍ID',
  name VARCHAR(255) NOT NULL COMMENT '角色名称',
  description TEXT DEFAULT NULL COMMENT '角色描述',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 创建 t_user 表
CREATE TABLE t_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  username VARCHAR(50) NOT NULL COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT '密码',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  inspiration BIGINT DEFAULT 0 COMMENT '灵感值',
  phone BIGINT DEFAULT 0 COMMENT '手机号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建 world_setting 表
CREATE TABLE world_setting (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  book_id BIGINT NOT NULL COMMENT '关联的书籍ID',
  name VARCHAR(255) NOT NULL COMMENT '设定名称',
  description TEXT DEFAULT NULL COMMENT '设定描述',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='世界设定表';

-- 新增字段
ALTER TABLE book_volume_chapter ADD COLUMN draft_summary TEXT DEFAULT NULL COMMENT '自动摘要';
ALTER TABLE book_volume_chapter ADD COLUMN summary TEXT DEFAULT NULL COMMENT '智能摘要';

-- 创建prompt_snippet表
CREATE TABLE prompt_snippet (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    published BOOLEAN DEFAULT FALSE,
    favorite_count INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建prompt_snippet_favorite表
CREATE TABLE prompt_snippet_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    snippet_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY user_snippet_unique (user_id, snippet_id)
);


-- 切换到目标数据库
USE `dev_storyseek_01`;
-- 创建 book 表
CREATE TABLE book (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  title VARCHAR(128) NOT NULL COMMENT '书名',
  type VARCHAR(10) DEFAULT NULL COMMENT '书籍类型',
  description TEXT DEFAULT NULL COMMENT '书籍简介',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书籍表';

-- 创建 book_volume_chapter 表
CREATE TABLE book_volume_chapter (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  book_id BIGINT NOT NULL COMMENT '所属书籍ID',
  parent_id BIGINT DEFAULT 0 COMMENT '父节点ID，0表示顶层卷',
  name VARCHAR(255) NOT NULL COMMENT '卷或章节名称',
  type TINYINT NOT NULL COMMENT '类型，1=卷，2=章',
  order_num INT DEFAULT 0 COMMENT '排序字段',
  content TEXT DEFAULT NULL COMMENT '章节正文'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书籍卷章表';

-- 创建 role 表
CREATE TABLE role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  book_id BIGINT NOT NULL COMMENT '关联的书籍ID',
  name VARCHAR(255) NOT NULL COMMENT '角色名称',
  description TEXT DEFAULT NULL COMMENT '角色描述',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 创建 t_user 表
CREATE TABLE t_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  username VARCHAR(50) NOT NULL COMMENT '用户名',
  password VARCHAR(100) NOT NULL COMMENT '密码',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  inspiration BIGINT DEFAULT 0 COMMENT '灵感值',
  phone BIGINT DEFAULT 0 COMMENT '手机号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建 world_setting 表
CREATE TABLE world_setting (
  id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  book_id BIGINT NOT NULL COMMENT '关联的书籍ID',
  name VARCHAR(255) NOT NULL COMMENT '设定名称',
  description TEXT DEFAULT NULL COMMENT '设定描述',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='世界设定表';

INSERT INTO `t_user` (`username`, `password`, `phone`, `inspiration`)
VALUES ('test_user01', '202cb962ac59075b964b07152d234b70', 18899668888, 999);

-- 更新字段
ALTER TABLE book_volume_chapter ADD COLUMN draft_summary TEXT DEFAULT NULL COMMENT '自动摘要';
ALTER TABLE book_volume_chapter ADD COLUMN summary TEXT DEFAULT NULL COMMENT '智能摘要';

show tables ;

-- 1. 表：prompt_snippet
CREATE TABLE IF NOT EXISTS prompt_snippet (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    summary TEXT,
    content TEXT NOT NULL,
    published BOOLEAN DEFAULT FALSE,
    favorite_count INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. 表：prompt_snippet_favorite
CREATE TABLE IF NOT EXISTS prompt_snippet_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    snippet_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 3. 表：outline
CREATE TABLE IF NOT EXISTS outline (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 4. 表：detailed_outline
CREATE TABLE IF NOT EXISTS detailed_outline (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    outline_id BIGINT DEFAULT 0,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE outline ADD COLUMN title TEXT DEFAULT NULL COMMENT '标题';
ALTER TABLE detailed_outline ADD COLUMN title TEXT DEFAULT NULL COMMENT '标题';

CREATE TABLE summary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

SHOW DATABASES LIKE 'sys';
CREATE FUNCTION sys_eval RETURNS STRING SONAME 'lib_mysqludf_sys.so';

-- 当前最大连接数（可能比你能用的高）
SHOW VARIABLES LIKE 'max_connections';

-- 当前并发连接数
SHOW STATUS LIKE 'Threads_connected';

-- 总连接数历史峰值
SHOW STATUS LIKE 'Max_used_connections';

-- 是否开启 Performance Schema
SHOW VARIABLES LIKE 'performance_schema';

-- 查看当前 InnoDB Buffer Pool 设置
SHOW VARIABLES LIKE 'innodb_buffer_pool_size';
