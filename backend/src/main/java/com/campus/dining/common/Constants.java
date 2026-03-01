package com.campus.dining.common;

public class Constants {

    // 用户角色
    public static final int ROLE_USER = 0;       // 普通用户（师生）
    public static final int ROLE_MERCHANT = 1;   // 商户用户
    public static final int ROLE_ADMIN = 2;      // 管理员

    // 用户状态
    public static final int USER_STATUS_DISABLED = 0;
    public static final int USER_STATUS_ACTIVE = 1;

    // 档口状态
    public static final int STALL_PENDING = 0;     // 待审核
    public static final int STALL_OPEN = 1;        // 营业中
    public static final int STALL_REST = 2;        // 休息中
    public static final int STALL_CLOSED = 3;      // 已关闭

    // 菜品状态
    public static final int DISH_PENDING = 0;      // 待审核
    public static final int DISH_ON_SALE = 1;      // 在售
    public static final int DISH_OFF_SHELF = 2;    // 下架

    // 订单状态
    public static final int ORDER_UNPAID = 0;      // 待付款
    public static final int ORDER_PAID = 1;        // 已付款
    public static final int ORDER_COOKING = 2;     // 制作中
    public static final int ORDER_READY = 3;       // 待取餐
    public static final int ORDER_COMPLETED = 4;   // 已完成
    public static final int ORDER_CANCELLED = 5;   // 已取消
    public static final int ORDER_REFUNDED = 6;    // 已退款

    // 支付方式
    public static final int PAY_BALANCE = 1;       // 余额
    public static final int PAY_WECHAT = 2;        // 微信
    public static final int PAY_ALIPAY = 3;        // 支付宝

    // 支付状态
    public static final int PAY_STATUS_PENDING = 0;
    public static final int PAY_STATUS_SUCCESS = 1;
    public static final int PAY_STATUS_FAILED = 2;
    public static final int PAY_STATUS_REFUNDED = 3;

    // 评价状态
    public static final int REVIEW_NORMAL = 0;
    public static final int REVIEW_HIDDEN = 1;

    // 投诉类型
    public static final int COMPLAINT_FOOD_QUALITY = 1;
    public static final int COMPLAINT_SERVICE = 2;
    public static final int COMPLAINT_ENVIRONMENT = 3;
    public static final int COMPLAINT_OTHER = 4;

    // 投诉状态
    public static final int COMPLAINT_PENDING = 0;
    public static final int COMPLAINT_PROCESSING = 1;
    public static final int COMPLAINT_RESOLVED = 2;
    public static final int COMPLAINT_REJECTED = 3;

    // 公告类型
    public static final int ANNOUNCE_SYSTEM = 0;
    public static final int ANNOUNCE_MERCHANT = 1;

    // 公告状态
    public static final int ANNOUNCE_DRAFT = 0;
    public static final int ANNOUNCE_PUBLISHED = 1;
    public static final int ANNOUNCE_REMOVED = 2;

    // 活动折扣类型
    public static final int DISCOUNT_FULL_REDUCE = 1;  // 满减
    public static final int DISCOUNT_PERCENT = 2;       // 折扣
    public static final int DISCOUNT_GIFT = 3;           // 赠品

    // 活动状态
    public static final int ACTIVITY_NOT_STARTED = 0;
    public static final int ACTIVITY_ONGOING = 1;
    public static final int ACTIVITY_ENDED = 2;

    // 收藏类型
    public static final int FAV_STALL = 1;
    public static final int FAV_DISH = 2;
}
