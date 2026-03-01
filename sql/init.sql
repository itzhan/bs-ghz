-- ============================================================
-- 校园餐饮管理系统 - 数据库初始化脚本
-- Database: campus_dining
-- ============================================================

CREATE DATABASE IF NOT EXISTS campus_dining DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE campus_dining;
SET NAMES utf8mb4;
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_RESULTS = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;

-- ============================================================
-- 删除已有表（按依赖关系逆序）
-- ============================================================

DROP TABLE IF EXISTS sys_config;
DROP TABLE IF EXISTS favorite;
DROP TABLE IF EXISTS cart_item;
DROP TABLE IF EXISTS activity;
DROP TABLE IF EXISTS announcement;
DROP TABLE IF EXISTS complaint;
DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS order_info;
DROP TABLE IF EXISTS dish;
DROP TABLE IF EXISTS dish_category;
DROP TABLE IF EXISTS stall;
DROP TABLE IF EXISTS sys_user;

-- ============================================================
-- 1. 用户表
-- ============================================================
CREATE TABLE sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名',
    password    VARCHAR(255) NOT NULL COMMENT '密码',
    nickname    VARCHAR(50)           COMMENT '昵称',
    phone       VARCHAR(20)           COMMENT '手机号',
    email       VARCHAR(100)          COMMENT '邮箱',
    avatar      VARCHAR(255)          COMMENT '头像URL',
    role        TINYINT      NOT NULL DEFAULT 0 COMMENT '角色: 0-普通用户 1-商户 2-管理员',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    balance     DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at  DATETIME              NULL COMMENT '删除时间（软删除）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- ============================================================
-- 2. 档口表
-- ============================================================
CREATE TABLE stall (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    merchant_id     BIGINT       NOT NULL COMMENT '商户用户ID',
    name            VARCHAR(100) NOT NULL COMMENT '档口名称',
    description     TEXT                  COMMENT '档口描述',
    logo            VARCHAR(255)          COMMENT '档口Logo',
    location        VARCHAR(100)          COMMENT '位置',
    phone           VARCHAR(20)           COMMENT '联系电话',
    business_hours  VARCHAR(50)           COMMENT '营业时间',
    status          TINYINT      NOT NULL DEFAULT 0 COMMENT '状态: 0-待审核 1-营业中 2-休息中 3-已关闭',
    avg_rating      DECIMAL(2,1) NOT NULL DEFAULT 0.0 COMMENT '平均评分',
    monthly_sales   INT          NOT NULL DEFAULT 0 COMMENT '月销量',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_merchant_id (merchant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='档口表';

-- ============================================================
-- 3. 菜品分类表
-- ============================================================
CREATE TABLE dish_category (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    stall_id    BIGINT      NOT NULL COMMENT '档口ID',
    name        VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort_order  INT         NOT NULL DEFAULT 0 COMMENT '排序号',
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_stall_id (stall_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜品分类表';

-- ============================================================
-- 4. 菜品表
-- ============================================================
CREATE TABLE dish (
    id                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    stall_id            BIGINT        NOT NULL COMMENT '档口ID',
    category_id         BIGINT                 COMMENT '分类ID',
    name                VARCHAR(100)  NOT NULL COMMENT '菜品名称',
    description         TEXT                   COMMENT '菜品描述',
    image               VARCHAR(255)           COMMENT '菜品图片',
    price               DECIMAL(8,2)  NOT NULL COMMENT '售价',
    original_price      DECIMAL(8,2)           COMMENT '原价',
    status              TINYINT       NOT NULL DEFAULT 0 COMMENT '状态: 0-待审核 1-在售 2-下架',
    stock               INT           NOT NULL DEFAULT 999 COMMENT '库存',
    daily_limit         INT           NOT NULL DEFAULT 999 COMMENT '每日限量',
    warning_threshold   INT           NOT NULL DEFAULT 10 COMMENT '库存预警阈值',
    monthly_sales       INT           NOT NULL DEFAULT 0 COMMENT '月销量',
    rating              DECIMAL(2,1)  NOT NULL DEFAULT 0.0 COMMENT '评分',
    is_recommended      TINYINT       NOT NULL DEFAULT 0 COMMENT '是否推荐: 0-否 1-是',
    created_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_stall_id (stall_id),
    INDEX idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜品表';

-- ============================================================
-- 5. 订单表
-- ============================================================
CREATE TABLE order_info (
    id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_no        VARCHAR(50)   NOT NULL COMMENT '订单编号',
    user_id         BIGINT        NOT NULL COMMENT '用户ID',
    stall_id        BIGINT        NOT NULL COMMENT '档口ID',
    total_amount    DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    actual_amount   DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    status          TINYINT       NOT NULL DEFAULT 0 COMMENT '状态: 0-待付款 1-已付款 2-制作中 3-待取餐 4-已完成 5-已取消 6-已退款',
    remark          VARCHAR(255)           COMMENT '备注',
    payment_method  TINYINT                COMMENT '支付方式: 1-余额 2-微信 3-支付宝',
    paid_at         DATETIME               NULL COMMENT '支付时间',
    completed_at    DATETIME               NULL COMMENT '完成时间',
    cancelled_at    DATETIME               NULL COMMENT '取消时间',
    cancel_reason   VARCHAR(255)           COMMENT '取消原因',
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_stall_id (stall_id),
    INDEX idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单表';

-- ============================================================
-- 6. 订单项表
-- ============================================================
CREATE TABLE order_item (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id    BIGINT        NOT NULL COMMENT '订单ID',
    dish_id     BIGINT        NOT NULL COMMENT '菜品ID',
    dish_name   VARCHAR(100)           COMMENT '菜品名称（冗余）',
    dish_image  VARCHAR(255)           COMMENT '菜品图片（冗余）',
    price       DECIMAL(8,2)           COMMENT '单价',
    quantity    INT                    COMMENT '数量',
    subtotal    DECIMAL(10,2)          COMMENT '小计',
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单项表';

-- ============================================================
-- 7. 支付记录表
-- ============================================================
CREATE TABLE payment (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id    BIGINT        NOT NULL COMMENT '订单ID',
    payment_no  VARCHAR(50)            COMMENT '支付流水号',
    amount      DECIMAL(10,2)          COMMENT '支付金额',
    method      TINYINT                COMMENT '支付方式: 1-余额 2-微信 3-支付宝',
    status      TINYINT       NOT NULL DEFAULT 0 COMMENT '状态: 0-待支付 1-支付成功 2-支付失败 3-已退款',
    paid_at     DATETIME               NULL COMMENT '支付时间',
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_payment_no (payment_no),
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='支付记录表';

-- ============================================================
-- 8. 评价表
-- ============================================================
CREATE TABLE review (
    id              BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id        BIGINT            COMMENT '订单ID',
    user_id         BIGINT   NOT NULL COMMENT '用户ID',
    stall_id        BIGINT   NOT NULL COMMENT '档口ID',
    dish_id         BIGINT            COMMENT '菜品ID',
    rating          TINYINT  NOT NULL COMMENT '评分 1-5',
    content         TEXT              COMMENT '评价内容',
    images          TEXT              COMMENT '评价图片（JSON数组）',
    reply_content   TEXT              COMMENT '商户回复内容',
    reply_at        DATETIME          NULL COMMENT '回复时间',
    status          TINYINT  NOT NULL DEFAULT 0 COMMENT '状态: 0-正常 1-隐藏',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_stall_id (stall_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='评价表';

-- ============================================================
-- 9. 投诉表
-- ============================================================
CREATE TABLE complaint (
    id              BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id         BIGINT   NOT NULL COMMENT '用户ID',
    stall_id        BIGINT   NOT NULL COMMENT '档口ID',
    order_id        BIGINT            COMMENT '订单ID',
    type            TINYINT  NOT NULL COMMENT '投诉类型: 1-食品质量 2-服务态度 3-环境卫生 4-其他',
    content         TEXT     NOT NULL COMMENT '投诉内容',
    images          TEXT              COMMENT '投诉图片（JSON数组）',
    status          TINYINT  NOT NULL DEFAULT 0 COMMENT '状态: 0-待处理 1-处理中 2-已解决 3-已驳回',
    reply_content   TEXT              COMMENT '回复内容',
    handler_id      BIGINT            COMMENT '处理人ID',
    handled_at      DATETIME          NULL COMMENT '处理时间',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='投诉表';

-- ============================================================
-- 10. 公告表
-- ============================================================
CREATE TABLE announcement (
    id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    title           VARCHAR(200)  NOT NULL COMMENT '公告标题',
    content         TEXT          NOT NULL COMMENT '公告内容',
    type            TINYINT       NOT NULL DEFAULT 0 COMMENT '类型: 0-系统公告 1-商户公告',
    publisher_id    BIGINT                 COMMENT '发布者ID',
    stall_id        BIGINT                 COMMENT '关联档口ID',
    status          TINYINT       NOT NULL DEFAULT 0 COMMENT '状态: 0-草稿 1-已发布 2-已下架',
    published_at    DATETIME               NULL COMMENT '发布时间',
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公告表';

-- ============================================================
-- 11. 活动表
-- ============================================================
CREATE TABLE activity (
    id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    stall_id        BIGINT        NOT NULL COMMENT '档口ID',
    title           VARCHAR(200)  NOT NULL COMMENT '活动标题',
    description     TEXT                   COMMENT '活动描述',
    image           VARCHAR(255)           COMMENT '活动图片',
    discount_type   TINYINT                COMMENT '优惠类型: 1-满减 2-折扣 3-赠品',
    discount_value  DECIMAL(8,2)           COMMENT '优惠值',
    min_amount      DECIMAL(8,2)           COMMENT '最低消费金额',
    start_time      DATETIME      NOT NULL COMMENT '开始时间',
    end_time        DATETIME      NOT NULL COMMENT '结束时间',
    status          TINYINT       NOT NULL DEFAULT 0 COMMENT '状态: 0-未开始 1-进行中 2-已结束',
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_stall_id (stall_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='活动表';

-- ============================================================
-- 12. 购物车表
-- ============================================================
CREATE TABLE cart_item (
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id     BIGINT   NOT NULL COMMENT '用户ID',
    dish_id     BIGINT   NOT NULL COMMENT '菜品ID',
    stall_id    BIGINT   NOT NULL COMMENT '档口ID',
    quantity    INT      NOT NULL DEFAULT 1 COMMENT '数量',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_dish (user_id, dish_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='购物车表';

-- ============================================================
-- 13. 收藏表
-- ============================================================
CREATE TABLE favorite (
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id     BIGINT   NOT NULL COMMENT '用户ID',
    stall_id    BIGINT            COMMENT '档口ID',
    dish_id     BIGINT            COMMENT '菜品ID',
    type        TINYINT  NOT NULL COMMENT '类型: 1-档口 2-菜品',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_target (user_id, stall_id, dish_id, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收藏表';

-- ============================================================
-- 14. 系统配置表
-- ============================================================
CREATE TABLE sys_config (
    id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    config_key      VARCHAR(100)  NOT NULL COMMENT '配置键',
    config_value    TEXT                   COMMENT '配置值',
    description     VARCHAR(255)           COMMENT '配置描述',
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统配置表';
