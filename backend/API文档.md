# 校园餐饮管理系统 API 文档

## 目录

- [概述](#概述)
- [基础信息](#基础信息)
- [认证授权](#认证授权)
- [用户接口](#用户接口)
- [公共接口](#公共接口)
- [需要认证的接口](#需要认证的接口)
- [商户接口](#商户接口)
- [管理员接口](#管理员接口)
- [数据模型](#数据模型)
- [测试账号](#测试账号)

---

## 概述

本文档描述了校园餐饮管理系统的 RESTful API 接口规范。系统支持普通用户、商户和管理员三种角色，提供完整的餐饮管理功能，包括档口管理、菜品管理、订单处理、评价投诉等。

---

## 基础信息

- **Base URL**: `http://localhost:8085`
- **认证方式**: JWT Bearer Token
- **Content-Type**: `application/json`
- **字符编码**: UTF-8

### 通用响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 分页响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

### 错误响应格式

```json
{
  "code": 400,
  "message": "错误信息",
  "data": null
}
```

### 认证头格式

```
Authorization: Bearer {token}
```

---

## 认证授权

### 1. 用户登录

**接口**: `POST /api/auth/login`

**描述**: 用户登录，获取访问令牌

**请求体**:
```json
{
  "username": "user1",
  "password": "123456"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "user1",
      "nickname": "用户1",
      "phone": "13800138000",
      "email": "user1@example.com",
      "avatar": "http://example.com/avatar.jpg",
      "role": 0,
      "status": 1,
      "balance": 100.00,
      "createdAt": "2024-01-01T10:00:00"
    }
  }
}
```

---

### 2. 用户注册

**接口**: `POST /api/auth/register`

**描述**: 新用户注册

**请求体**:
```json
{
  "username": "newuser",
  "password": "123456",
  "nickname": "新用户",
  "phone": "13800138001",
  "email": "newuser@example.com",
  "role": 0
}
```

**参数说明**:
- `username`: 用户名（必填）
- `password`: 密码（必填）
- `nickname`: 昵称（必填）
- `phone`: 手机号（可选）
- `email`: 邮箱（可选）
- `role`: 角色（0-普通用户，1-商户）

**响应示例**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 21,
    "username": "newuser",
    "nickname": "新用户",
    "phone": "13800138001",
    "email": "newuser@example.com",
    "role": 0,
    "status": 1,
    "balance": 0.00,
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

---

### 3. 获取当前用户信息

**接口**: `GET /api/auth/current`

**描述**: 获取当前登录用户信息

**认证**: 需要 Bearer Token

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "user1",
    "nickname": "用户1",
    "phone": "13800138000",
    "email": "user1@example.com",
    "avatar": "http://example.com/avatar.jpg",
    "role": 0,
    "status": 1,
    "balance": 100.00,
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

---

## 用户接口

### 1. 获取用户资料

**接口**: `GET /api/users/profile`

**描述**: 获取当前用户的详细资料

**认证**: 需要 Bearer Token

**响应示例**: 同 `/api/auth/current`

---

### 2. 更新用户资料

**接口**: `PUT /api/users/profile`

**描述**: 更新用户个人信息

**认证**: 需要 Bearer Token

**请求体**:
```json
{
  "nickname": "新昵称",
  "phone": "13800138002",
  "email": "newemail@example.com",
  "avatar": "http://example.com/new-avatar.jpg",
  "oldPassword": "123456",
  "newPassword": "newpassword123"
}
```

**参数说明**:
- 所有字段均为可选
- 修改密码时需要同时提供 `oldPassword` 和 `newPassword`

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "username": "user1",
    "nickname": "新昵称",
    "phone": "13800138002",
    "email": "newemail@example.com",
    "avatar": "http://example.com/new-avatar.jpg",
    "role": 0,
    "status": 1,
    "balance": 100.00,
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

---

### 3. 账户充值

**接口**: `POST /api/users/recharge`

**描述**: 用户账户充值

**认证**: 需要 Bearer Token

**请求体**:
```json
{
  "amount": 100.00
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "充值成功",
  "data": null
}
```

---

## 公共接口

### 档口相关

#### 1. 获取档口列表

**接口**: `GET /api/stalls`

**描述**: 分页查询档口列表

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）
- `keyword`: 关键词搜索（可选）
- `status`: 状态筛选（可选）

**请求示例**:
```
GET /api/stalls?page=1&size=10&keyword=川菜&status=1
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "merchantId": 2,
        "name": "川味小厨",
        "description": "正宗川菜，麻辣鲜香",
        "logo": "http://example.com/logo.jpg",
        "location": "食堂一楼A区",
        "phone": "13800138000",
        "businessHours": "10:00-21:00",
        "status": 1,
        "avgRating": 4.5,
        "monthlySales": 500,
        "createdAt": "2024-01-01T10:00:00",
        "updatedAt": "2024-01-15T10:00:00"
      }
    ],
    "total": 50,
    "page": 1,
    "size": 10
  }
}
```

---

#### 2. 获取档口详情

**接口**: `GET /api/stalls/{id}`

**描述**: 获取指定档口的详细信息

**路径参数**:
- `id`: 档口ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "merchantId": 2,
    "name": "川味小厨",
    "description": "正宗川菜，麻辣鲜香",
    "logo": "http://example.com/logo.jpg",
    "location": "食堂一楼A区",
    "phone": "13800138000",
    "businessHours": "10:00-21:00",
    "status": 1,
    "avgRating": 4.5,
    "monthlySales": 500,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-15T10:00:00"
  }
}
```

---

#### 3. 获取档口菜品列表

**接口**: `GET /api/stalls/{id}/dishes`

**描述**: 获取指定档口的菜品列表

**路径参数**:
- `id`: 档口ID

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）
- `categoryId`: 分类ID（可选）
- `keyword`: 关键词搜索（可选）

**响应示例**: 同 `/api/dishes` 的分页响应格式

---

#### 4. 获取档口分类列表

**接口**: `GET /api/stalls/{id}/categories`

**描述**: 获取指定档口的菜品分类列表

**路径参数**:
- `id`: 档口ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "stallId": 1,
      "name": "热菜",
      "sortOrder": 1,
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00"
    },
    {
      "id": 2,
      "stallId": 1,
      "name": "凉菜",
      "sortOrder": 2,
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00"
    }
  ]
}
```

---

#### 5. 获取档口评价列表

**接口**: `GET /api/stalls/{id}/reviews`

**描述**: 获取指定档口的评价列表

**路径参数**:
- `id`: 档口ID

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）

**响应示例**: 同 `/api/reviews` 的分页响应格式

---

#### 6. 获取档口活动列表

**接口**: `GET /api/stalls/{id}/activities`

**描述**: 获取指定档口的活动列表

**路径参数**:
- `id`: 档口ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "stallId": 1,
      "title": "满30减5",
      "description": "满30元立减5元",
      "image": "http://example.com/activity.jpg",
      "discountType": 1,
      "discountValue": 5.00,
      "minAmount": 30.00,
      "startTime": "2024-01-01T00:00:00",
      "endTime": "2024-01-31T23:59:59",
      "status": 1,
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00"
    }
  ]
}
```

---

### 菜品相关

#### 1. 获取菜品列表

**接口**: `GET /api/dishes`

**描述**: 分页查询菜品列表

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）
- `stallId`: 档口ID（可选）
- `categoryId`: 分类ID（可选）
- `keyword`: 关键词搜索（可选）
- `status`: 状态筛选（可选）

**请求示例**:
```
GET /api/dishes?page=1&size=10&stallId=1&categoryId=1&keyword=宫保鸡丁
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "stallId": 1,
        "categoryId": 1,
        "name": "宫保鸡丁",
        "description": "经典川菜，鸡肉鲜嫩，花生酥脆",
        "image": "http://example.com/dish.jpg",
        "price": 18.00,
        "originalPrice": 20.00,
        "status": 1,
        "stock": 50,
        "dailyLimit": 100,
        "warningThreshold": 10,
        "monthlySales": 200,
        "rating": 4.8,
        "isRecommended": true,
        "createdAt": "2024-01-01T10:00:00",
        "updatedAt": "2024-01-15T10:00:00"
      }
    ],
    "total": 30,
    "page": 1,
    "size": 10
  }
}
```

---

#### 2. 获取菜品详情

**接口**: `GET /api/dishes/{id}`

**描述**: 获取指定菜品的详细信息

**路径参数**:
- `id`: 菜品ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "stallId": 1,
    "categoryId": 1,
    "name": "宫保鸡丁",
    "description": "经典川菜，鸡肉鲜嫩，花生酥脆",
    "image": "http://example.com/dish.jpg",
    "price": 18.00,
    "originalPrice": 20.00,
    "status": 1,
    "stock": 50,
    "dailyLimit": 100,
    "warningThreshold": 10,
    "monthlySales": 200,
    "rating": 4.8,
    "isRecommended": true,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-15T10:00:00"
  }
}
```

---

### 分类相关

#### 获取分类列表

**接口**: `GET /api/dish-categories`

**描述**: 获取菜品分类列表

**查询参数**:
- `stallId`: 档口ID（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "stallId": 1,
      "name": "热菜",
      "sortOrder": 1,
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00"
    }
  ]
}
```

---

### 公告相关

#### 1. 获取公告列表

**接口**: `GET /api/announcements`

**描述**: 分页查询公告列表

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）
- `type`: 类型（0-系统公告，1-商户公告）
- `status`: 状态（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "系统维护通知",
        "content": "系统将于今晚22:00-24:00进行维护",
        "type": 0,
        "publisherId": 1,
        "stallId": null,
        "status": 1,
        "publishedAt": "2024-01-01T10:00:00",
        "createdAt": "2024-01-01T09:00:00",
        "updatedAt": "2024-01-01T10:00:00"
      }
    ],
    "total": 20,
    "page": 1,
    "size": 10
  }
}
```

---

#### 2. 获取公告详情

**接口**: `GET /api/announcements/{id}`

**描述**: 获取指定公告的详细信息

**路径参数**:
- `id`: 公告ID

**响应示例**: 同公告列表中的单个公告对象

---

### 活动相关

#### 1. 获取活动列表

**接口**: `GET /api/activities`

**描述**: 分页查询活动列表

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）
- `stallId`: 档口ID（可选）
- `status`: 状态（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "stallId": 1,
        "title": "满30减5",
        "description": "满30元立减5元",
        "image": "http://example.com/activity.jpg",
        "discountType": 1,
        "discountValue": 5.00,
        "minAmount": 30.00,
        "startTime": "2024-01-01T00:00:00",
        "endTime": "2024-01-31T23:59:59",
        "status": 1,
        "createdAt": "2024-01-01T10:00:00",
        "updatedAt": "2024-01-01T10:00:00"
      }
    ],
    "total": 10,
    "page": 1,
    "size": 10
  }
}
```

---

#### 2. 获取活动详情

**接口**: `GET /api/activities/{id}`

**描述**: 获取指定活动的详细信息

**路径参数**:
- `id`: 活动ID

**响应示例**: 同活动列表中的单个活动对象

---

### 评价相关

#### 获取评价列表

**接口**: `GET /api/reviews`

**描述**: 分页查询评价列表

**查询参数**:
- `stallId`: 档口ID（可选）
- `dishId`: 菜品ID（可选）
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "orderId": 100,
        "userId": 1,
        "stallId": 1,
        "dishId": 1,
        "rating": 5,
        "content": "味道很好，服务也不错",
        "images": ["http://example.com/review1.jpg"],
        "replyContent": "感谢您的支持！",
        "replyAt": "2024-01-02T10:00:00",
        "status": 0,
        "createdAt": "2024-01-01T10:00:00",
        "updatedAt": "2024-01-02T10:00:00"
      }
    ],
    "total": 50,
    "page": 1,
    "size": 10
  }
}
```

---

### 统计相关

#### 1. 获取仪表盘统计

**接口**: `GET /api/stats/dashboard`

**描述**: 获取系统仪表盘统计数据

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalUsers": 1000,
    "totalStalls": 50,
    "totalOrders": 5000,
    "totalRevenue": 100000.00
  }
}
```

---

#### 2. 获取菜品排行榜

**接口**: `GET /api/stats/dishes/ranking`

**描述**: 获取菜品销售排行榜

**查询参数**:
- `stallId`: 档口ID（可选）
- `limit`: 返回数量（默认10）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "dishId": 1,
      "dishName": "宫保鸡丁",
      "sales": 500,
      "revenue": 9000.00
    },
    {
      "dishId": 2,
      "dishName": "麻婆豆腐",
      "sales": 400,
      "revenue": 6000.00
    }
  ]
}
```

---

## 需要认证的接口

### 订单相关

#### 1. 创建订单

**接口**: `POST /api/orders`

**描述**: 创建新订单

**认证**: 需要 Bearer Token

**请求体**:
```json
{
  "stallId": 1,
  "remark": "不要香菜",
  "paymentMethod": 1,
  "items": [
    {
      "dishId": 1,
      "quantity": 2
    },
    {
      "dishId": 2,
      "quantity": 1
    }
  ]
}
```

**参数说明**:
- `stallId`: 档口ID（必填）
- `remark`: 备注（可选）
- `paymentMethod`: 支付方式（1-余额，2-微信，3-支付宝）
- `items`: 订单项数组（必填）
  - `dishId`: 菜品ID
  - `quantity`: 数量

**响应示例**:
```json
{
  "code": 200,
  "message": "订单创建成功",
  "data": {
    "id": 100,
    "orderNo": "ORD20240101100001",
    "userId": 1,
    "stallId": 1,
    "totalAmount": 54.00,
    "actualAmount": 49.00,
    "status": 1,
    "remark": "不要香菜",
    "paymentMethod": 1,
    "paidAt": "2024-01-01T10:00:00",
    "completedAt": null,
    "cancelledAt": null,
    "cancelReason": null,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

---

#### 2. 获取订单列表

**接口**: `GET /api/orders`

**描述**: 获取当前用户的订单列表

**认证**: 需要 Bearer Token

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）
- `status`: 状态筛选（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 100,
        "orderNo": "ORD20240101100001",
        "userId": 1,
        "stallId": 1,
        "totalAmount": 54.00,
        "actualAmount": 49.00,
        "status": 4,
        "remark": "不要香菜",
        "paymentMethod": 1,
        "paidAt": "2024-01-01T10:00:00",
        "completedAt": "2024-01-01T10:30:00",
        "cancelledAt": null,
        "cancelReason": null,
        "createdAt": "2024-01-01T10:00:00",
        "updatedAt": "2024-01-01T10:30:00"
      }
    ],
    "total": 20,
    "page": 1,
    "size": 10
  }
}
```

---

#### 3. 获取订单详情

**接口**: `GET /api/orders/{id}`

**描述**: 获取指定订单的详细信息（包含订单项）

**认证**: 需要 Bearer Token

**路径参数**:
- `id`: 订单ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "order": {
      "id": 100,
      "orderNo": "ORD20240101100001",
      "userId": 1,
      "stallId": 1,
      "totalAmount": 54.00,
      "actualAmount": 49.00,
      "status": 4,
      "remark": "不要香菜",
      "paymentMethod": 1,
      "paidAt": "2024-01-01T10:00:00",
      "completedAt": "2024-01-01T10:30:00",
      "cancelledAt": null,
      "cancelReason": null,
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:30:00"
    },
    "items": [
      {
        "id": 1,
        "orderId": 100,
        "dishId": 1,
        "dishName": "宫保鸡丁",
        "dishImage": "http://example.com/dish.jpg",
        "price": 18.00,
        "quantity": 2,
        "subtotal": 36.00,
        "createdAt": "2024-01-01T10:00:00"
      },
      {
        "id": 2,
        "orderId": 100,
        "dishId": 2,
        "dishName": "麻婆豆腐",
        "dishImage": "http://example.com/dish2.jpg",
        "price": 13.00,
        "quantity": 1,
        "subtotal": 13.00,
        "createdAt": "2024-01-01T10:00:00"
      }
    ]
  }
}
```

---

#### 4. 获取订单项列表

**接口**: `GET /api/orders/{id}/items`

**描述**: 获取指定订单的订单项列表

**认证**: 需要 Bearer Token

**路径参数**:
- `id`: 订单ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "orderId": 100,
      "dishId": 1,
      "dishName": "宫保鸡丁",
      "dishImage": "http://example.com/dish.jpg",
      "price": 18.00,
      "quantity": 2,
      "subtotal": 36.00,
      "createdAt": "2024-01-01T10:00:00"
    }
  ]
}
```

---

#### 5. 取消订单

**接口**: `PUT /api/orders/{id}/cancel`

**描述**: 取消指定订单

**认证**: 需要 Bearer Token

**路径参数**:
- `id`: 订单ID

**请求体**:
```json
{
  "reason": "临时有事，不需要了"
}
```

**参数说明**:
- `reason`: 取消原因（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "订单已取消",
  "data": null
}
```

---

### 购物车相关

#### 1. 获取购物车列表

**接口**: `GET /api/cart`

**描述**: 获取当前用户的购物车列表

**认证**: 需要 Bearer Token

**查询参数**:
- `stallId`: 档口ID（可选，用于筛选特定档口的购物车项）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "dishId": 1,
      "stallId": 1,
      "quantity": 2,
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00"
    }
  ]
}
```

---

#### 2. 添加购物车项

**接口**: `POST /api/cart`

**描述**: 添加商品到购物车

**认证**: 需要 Bearer Token

**请求体**:
```json
{
  "dishId": 1,
  "quantity": 2
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "id": 1,
    "userId": 1,
    "dishId": 1,
    "stallId": 1,
    "quantity": 2,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

---

#### 3. 更新购物车项

**接口**: `PUT /api/cart/{id}`

**描述**: 更新购物车项数量

**认证**: 需要 Bearer Token

**路径参数**:
- `id`: 购物车项ID

**请求体**:
```json
{
  "quantity": 3
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

#### 4. 删除购物车项

**接口**: `DELETE /api/cart/{id}`

**描述**: 删除指定购物车项

**认证**: 需要 Bearer Token

**路径参数**:
- `id`: 购物车项ID

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

#### 5. 清空购物车

**接口**: `DELETE /api/cart/clear`

**描述**: 清空购物车（可指定档口）

**认证**: 需要 Bearer Token

**查询参数**:
- `stallId`: 档口ID（可选，不传则清空所有）

**响应示例**:
```json
{
  "code": 200,
  "message": "清空成功",
  "data": null
}
```

---

### 评价相关（创建）

#### 1. 创建评价

**接口**: `POST /api/reviews`

**描述**: 创建订单评价

**认证**: 需要 Bearer Token

**请求体**:
```json
{
  "orderId": 100,
  "dishId": 1,
  "rating": 5,
  "content": "味道很好，服务也不错",
  "images": ["http://example.com/review1.jpg", "http://example.com/review2.jpg"]
}
```

**参数说明**:
- `orderId`: 订单ID（必填）
- `dishId`: 菜品ID（可选，不填则为档口评价）
- `rating`: 评分（1-5，必填）
- `content`: 评价内容（可选）
- `images`: 评价图片数组（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "评价成功",
  "data": {
    "id": 1,
    "orderId": 100,
    "userId": 1,
    "stallId": 1,
    "dishId": 1,
    "rating": 5,
    "content": "味道很好，服务也不错",
    "images": ["http://example.com/review1.jpg", "http://example.com/review2.jpg"],
    "replyContent": null,
    "replyAt": null,
    "status": 0,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

---

#### 2. 获取我的评价列表

**接口**: `GET /api/reviews/mine`

**描述**: 获取当前用户的评价列表

**认证**: 需要 Bearer Token

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）

**响应示例**: 同 `/api/reviews` 的分页响应格式

---

### 投诉相关

#### 1. 创建投诉

**接口**: `POST /api/complaints`

**描述**: 创建投诉

**认证**: 需要 Bearer Token

**请求体**:
```json
{
  "stallId": 1,
  "orderId": 100,
  "type": 1,
  "content": "菜品质量有问题，味道不对",
  "images": ["http://example.com/complaint1.jpg"]
}
```

**参数说明**:
- `stallId`: 档口ID（必填）
- `orderId`: 订单ID（可选）
- `type`: 投诉类型（1-食品质量，2-服务态度，3-环境卫生，4-其他）
- `content`: 投诉内容（必填）
- `images`: 投诉图片数组（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "投诉提交成功",
  "data": {
    "id": 1,
    "userId": 1,
    "stallId": 1,
    "orderId": 100,
    "type": 1,
    "content": "菜品质量有问题，味道不对",
    "images": ["http://example.com/complaint1.jpg"],
    "status": 0,
    "replyContent": null,
    "handlerId": null,
    "handledAt": null,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

---

#### 2. 获取我的投诉列表

**接口**: `GET /api/complaints/mine`

**描述**: 获取当前用户的投诉列表

**认证**: 需要 Bearer Token

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 1,
        "stallId": 1,
        "orderId": 100,
        "type": 1,
        "content": "菜品质量有问题，味道不对",
        "images": ["http://example.com/complaint1.jpg"],
        "status": 2,
        "replyContent": "已处理，已联系档口改进",
        "handlerId": 1,
        "handledAt": "2024-01-02T10:00:00",
        "createdAt": "2024-01-01T10:00:00",
        "updatedAt": "2024-01-02T10:00:00"
      }
    ],
    "total": 5,
    "page": 1,
    "size": 10
  }
}
```

---

### 收藏相关

#### 1. 添加收藏

**接口**: `POST /api/favorites`

**描述**: 添加收藏（档口或菜品）

**认证**: 需要 Bearer Token

**请求体**:
```json
{
  "stallId": 1,
  "type": 1
}
```

或

```json
{
  "dishId": 1,
  "type": 2
}
```

**参数说明**:
- `stallId`: 档口ID（type=1时必填）
- `dishId`: 菜品ID（type=2时必填）
- `type`: 类型（1-档口，2-菜品）

**响应示例**:
```json
{
  "code": 200,
  "message": "收藏成功",
  "data": {
    "id": 1,
    "userId": 1,
    "stallId": 1,
    "dishId": null,
    "type": 1,
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

---

#### 2. 取消收藏

**接口**: `DELETE /api/favorites/{id}`

**描述**: 取消收藏

**认证**: 需要 Bearer Token

**路径参数**:
- `id`: 收藏ID

**响应示例**:
```json
{
  "code": 200,
  "message": "取消收藏成功",
  "data": null
}
```

---

#### 3. 获取收藏列表

**接口**: `GET /api/favorites`

**描述**: 获取当前用户的收藏列表

**认证**: 需要 Bearer Token

**查询参数**:
- `type`: 类型（1-档口，2-菜品，可选）
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 1,
        "stallId": 1,
        "dishId": null,
        "type": 1,
        "createdAt": "2024-01-01T10:00:00"
      }
    ],
    "total": 10,
    "page": 1,
    "size": 10
  }
}
```

---

#### 4. 检查是否已收藏

**接口**: `GET /api/favorites/check`

**描述**: 检查指定目标是否已收藏

**认证**: 需要 Bearer Token

**查询参数**:
- `targetId`: 目标ID（档口ID或菜品ID）
- `type`: 类型（1-档口，2-菜品）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

## 商户接口

> **注意**: 所有商户接口都需要 MERCHANT 角色权限

### 档口管理

#### 1. 获取我的档口列表

**接口**: `GET /api/merchant/stalls`

**描述**: 获取当前商户的档口列表

**认证**: 需要 Bearer Token + MERCHANT 角色

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "merchantId": 2,
      "name": "川味小厨",
      "description": "正宗川菜，麻辣鲜香",
      "logo": "http://example.com/logo.jpg",
      "location": "食堂一楼A区",
      "phone": "13800138000",
      "businessHours": "10:00-21:00",
      "status": 1,
      "avgRating": 4.5,
      "monthlySales": 500,
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-15T10:00:00"
    }
  ]
}
```

---

#### 2. 创建档口

**接口**: `POST /api/merchant/stalls`

**描述**: 创建新档口

**认证**: 需要 Bearer Token + MERCHANT 角色

**请求体**:
```json
{
  "name": "新档口",
  "description": "档口描述",
  "logo": "http://example.com/logo.jpg",
  "location": "食堂二楼B区",
  "phone": "13800138001",
  "businessHours": "10:00-21:00"
}
```

**响应示例**: 同获取档口详情

---

#### 3. 更新档口

**接口**: `PUT /api/merchant/stalls/{id}`

**描述**: 更新档口信息

**认证**: 需要 Bearer Token + MERCHANT 角色

**路径参数**:
- `id`: 档口ID

**请求体**: 同创建档口

**响应示例**: 同获取档口详情

---

#### 4. 更新档口状态

**接口**: `PUT /api/merchant/stalls/{id}/status`

**描述**: 更新档口营业状态

**认证**: 需要 Bearer Token + MERCHANT 角色

**路径参数**:
- `id`: 档口ID

**请求体**:
```json
{
  "status": 2
}
```

**参数说明**:
- `status`: 状态（1-营业中，2-休息中，3-已关闭）

**响应示例**:
```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": null
}
```

---

### 菜品管理

#### 1. 创建菜品

**接口**: `POST /api/merchant/dishes`

**描述**: 创建新菜品

**认证**: 需要 Bearer Token + MERCHANT 角色

**请求体**:
```json
{
  "stallId": 1,
  "categoryId": 1,
  "name": "新菜品",
  "description": "菜品描述",
  "image": "http://example.com/dish.jpg",
  "price": 20.00,
  "originalPrice": 25.00,
  "stock": 100,
  "dailyLimit": 200,
  "warningThreshold": 20,
  "isRecommended": false
}
```

**响应示例**: 同获取菜品详情

---

#### 2. 更新菜品

**接口**: `PUT /api/merchant/dishes/{id}`

**描述**: 更新菜品信息

**认证**: 需要 Bearer Token + MERCHANT 角色

**路径参数**:
- `id`: 菜品ID

**请求体**: 同创建菜品

**响应示例**: 同获取菜品详情

---

#### 3. 更新菜品状态

**接口**: `PUT /api/merchant/dishes/{id}/status`

**描述**: 更新菜品上架/下架状态

**认证**: 需要 Bearer Token + MERCHANT 角色

**路径参数**:
- `id`: 菜品ID

**请求体**:
```json
{
  "status": 2
}
```

**参数说明**:
- `status`: 状态（1-在售，2-下架）

**响应示例**:
```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": null
}
```

---

#### 4. 更新菜品库存

**接口**: `PUT /api/merchant/dishes/{id}/stock`

**描述**: 更新菜品库存

**认证**: 需要 Bearer Token + MERCHANT 角色

**路径参数**:
- `id`: 菜品ID

**请求体**:
```json
{
  "stock": 150
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "库存更新成功",
  "data": null
}
```

---

### 分类管理

#### 1. 创建分类

**接口**: `POST /api/merchant/categories`

**描述**: 创建菜品分类

**认证**: 需要 Bearer Token + MERCHANT 角色

**请求体**:
```json
{
  "stallId": 1,
  "name": "新分类",
  "sortOrder": 1
}
```

**响应示例**: 同获取分类列表中的单个分类对象

---

#### 2. 更新分类

**接口**: `PUT /api/merchant/categories/{id}`

**描述**: 更新分类信息

**认证**: 需要 Bearer Token + MERCHANT 角色

**路径参数**:
- `id`: 分类ID

**请求体**: 同创建分类

**响应示例**: 同获取分类列表中的单个分类对象

---

#### 3. 删除分类

**接口**: `DELETE /api/merchant/categories/{id}`

**描述**: 删除分类

**认证**: 需要 Bearer Token + MERCHANT 角色

**路径参数**:
- `id`: 分类ID

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

### 订单管理

#### 1. 获取订单列表

**接口**: `GET /api/merchant/orders`

**描述**: 获取商户的订单列表

**认证**: 需要 Bearer Token + MERCHANT 角色

**查询参数**:
- `stallId`: 档口ID（可选）
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）
- `status`: 状态筛选（可选）

**响应示例**: 同用户订单列表的分页响应格式

---

#### 2. 更新订单状态

**接口**: `PUT /api/merchant/orders/{id}/status`

**描述**: 更新订单状态（接单、制作中、待取餐、已完成等）

**认证**: 需要 Bearer Token + MERCHANT 角色

**路径参数**:
- `id`: 订单ID

**请求体**:
```json
{
  "status": 3
}
```

**参数说明**:
- `status`: 订单状态（2-制作中，3-待取餐，4-已完成）

**响应示例**:
```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": null
}
```

---

### 评价管理

#### 回复评价

**接口**: `PUT /api/merchant/reviews/{id}/reply`

**描述**: 回复用户评价

**认证**: 需要 Bearer Token + MERCHANT 角色

**路径参数**:
- `id`: 评价ID

**请求体**:
```json
{
  "replyContent": "感谢您的支持，我们会继续努力！"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "回复成功",
  "data": null
}
```

---

### 公告管理

#### 1. 创建公告

**接口**: `POST /api/merchant/announcements`

**描述**: 创建商户公告

**认证**: 需要 Bearer Token + MERCHANT 角色

**请求体**:
```json
{
  "title": "春节营业时间调整",
  "content": "春节期间营业时间调整为10:00-20:00",
  "type": 1,
  "stallId": 1
}
```

**响应示例**: 同获取公告详情

---

#### 2. 更新公告

**接口**: `PUT /api/merchant/announcements/{id}`

**描述**: 更新公告信息

**认证**: 需要 Bearer Token + MERCHANT 角色

**路径参数**:
- `id`: 公告ID

**请求体**: 同创建公告

**响应示例**: 同获取公告详情

---

#### 3. 发布公告

**接口**: `PUT /api/merchant/announcements/{id}/publish`

**描述**: 发布公告

**认证**: 需要 Bearer Token + MERCHANT 角色

**路径参数**:
- `id`: 公告ID

**响应示例**:
```json
{
  "code": 200,
  "message": "发布成功",
  "data": null
}
```

---

#### 4. 下架公告

**接口**: `PUT /api/merchant/announcements/{id}/takedown`

**描述**: 下架公告

**认证**: 需要 Bearer Token + MERCHANT 角色

**路径参数**:
- `id`: 公告ID

**响应示例**:
```json
{
  "code": 200,
  "message": "下架成功",
  "data": null
}
```

---

### 活动管理

#### 1. 创建活动

**接口**: `POST /api/merchant/activities`

**描述**: 创建营销活动

**认证**: 需要 Bearer Token + MERCHANT 角色

**请求体**:
```json
{
  "stallId": 1,
  "title": "满30减5",
  "description": "满30元立减5元",
  "image": "http://example.com/activity.jpg",
  "discountType": 1,
  "discountValue": 5.00,
  "minAmount": 30.00,
  "startTime": "2024-01-01T00:00:00",
  "endTime": "2024-01-31T23:59:59"
}
```

**参数说明**:
- `discountType`: 优惠类型（1-满减，2-折扣，3-赠品）
- `discountValue`: 优惠值（满减金额、折扣比例等）
- `minAmount`: 最低消费金额
- `startTime`: 开始时间
- `endTime`: 结束时间

**响应示例**: 同获取活动详情

---

#### 2. 更新活动

**接口**: `PUT /api/merchant/activities/{id}`

**描述**: 更新活动信息

**认证**: 需要 Bearer Token + MERCHANT 角色

**路径参数**:
- `id`: 活动ID

**请求体**: 同创建活动

**响应示例**: 同获取活动详情

---

#### 3. 删除活动

**接口**: `DELETE /api/merchant/activities/{id}`

**描述**: 删除活动

**认证**: 需要 Bearer Token + MERCHANT 角色

**路径参数**:
- `id`: 活动ID

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

### 统计报表

#### 1. 获取统计概览

**接口**: `GET /api/merchant/stats`

**描述**: 获取商户统计概览

**认证**: 需要 Bearer Token + MERCHANT 角色

**查询参数**:
- `stallId`: 档口ID（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "revenue": 50000.00,
    "orderCount": 1000,
    "avgRating": 4.5
  }
}
```

---

#### 2. 获取菜品排行榜

**接口**: `GET /api/merchant/stats/dishes`

**描述**: 获取菜品销售排行榜

**认证**: 需要 Bearer Token + MERCHANT 角色

**查询参数**:
- `stallId`: 档口ID（可选）
- `limit`: 返回数量（默认10）

**响应示例**: 同 `/api/stats/dishes/ranking`

---

#### 3. 获取营收统计

**接口**: `GET /api/merchant/stats/revenue`

**描述**: 获取营收统计数据

**认证**: 需要 Bearer Token + MERCHANT 角色

**查询参数**:
- `stallId`: 档口ID（可选）
- `startDate`: 开始日期（可选，格式：YYYY-MM-DD）
- `endDate`: 结束日期（可选，格式：YYYY-MM-DD）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "date": "2024-01-01",
      "revenue": 1000.00,
      "orderCount": 50
    },
    {
      "date": "2024-01-02",
      "revenue": 1200.00,
      "orderCount": 60
    }
  ]
}
```

---

## 管理员接口

> **注意**: 所有管理员接口都需要 ADMIN 角色权限

### 用户管理

#### 1. 获取用户列表

**接口**: `GET /api/admin/users`

**描述**: 分页查询用户列表

**认证**: 需要 Bearer Token + ADMIN 角色

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）
- `keyword`: 关键词搜索（可选）
- `role`: 角色筛选（可选）
- `status`: 状态筛选（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "username": "user1",
        "nickname": "用户1",
        "phone": "13800138000",
        "email": "user1@example.com",
        "avatar": "http://example.com/avatar.jpg",
        "role": 0,
        "status": 1,
        "balance": 100.00,
        "createdAt": "2024-01-01T10:00:00"
      }
    ],
    "total": 1000,
    "page": 1,
    "size": 10
  }
}
```

---

#### 2. 更新用户状态

**接口**: `PUT /api/admin/users/{id}/status`

**描述**: 启用/禁用用户

**认证**: 需要 Bearer Token + ADMIN 角色

**路径参数**:
- `id`: 用户ID

**请求体**:
```json
{
  "status": 0
}
```

**参数说明**:
- `status`: 状态（0-禁用，1-正常）

**响应示例**:
```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": null
}
```

---

#### 3. 删除用户

**接口**: `DELETE /api/admin/users/{id}`

**描述**: 删除用户

**认证**: 需要 Bearer Token + ADMIN 角色

**路径参数**:
- `id`: 用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

### 档口审核

#### 审核档口

**接口**: `PUT /api/admin/stalls/{id}/audit`

**描述**: 审核档口（通过/拒绝）

**认证**: 需要 Bearer Token + ADMIN 角色

**路径参数**:
- `id`: 档口ID

**请求体**:
```json
{
  "status": 1
}
```

**参数说明**:
- `status`: 审核状态（1-通过，3-拒绝）

**响应示例**:
```json
{
  "code": 200,
  "message": "审核完成",
  "data": null
}
```

---

### 菜品审核

#### 审核菜品

**接口**: `PUT /api/admin/dishes/{id}/audit`

**描述**: 审核菜品（通过/拒绝）

**认证**: 需要 Bearer Token + ADMIN 角色

**路径参数**:
- `id`: 菜品ID

**请求体**:
```json
{
  "status": 1
}
```

**参数说明**:
- `status`: 审核状态（1-通过，2-拒绝）

**响应示例**:
```json
{
  "code": 200,
  "message": "审核完成",
  "data": null
}
```

---

### 订单管理

#### 1. 获取订单列表

**接口**: `GET /api/admin/orders`

**描述**: 获取所有订单列表

**认证**: 需要 Bearer Token + ADMIN 角色

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）
- `status`: 状态筛选（可选）
- `keyword`: 关键词搜索（可选）

**响应示例**: 同用户订单列表的分页响应格式

---

#### 2. 更新订单状态

**接口**: `PUT /api/admin/orders/{id}/status`

**描述**: 管理员更新订单状态

**认证**: 需要 Bearer Token + ADMIN 角色

**路径参数**:
- `id`: 订单ID

**请求体**:
```json
{
  "status": 4
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": null
}
```

---

### 支付管理

#### 获取支付记录列表

**接口**: `GET /api/admin/payments`

**描述**: 获取支付记录列表

**认证**: 需要 Bearer Token + ADMIN 角色

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "orderId": 100,
        "paymentNo": "PAY20240101100001",
        "amount": 49.00,
        "method": 1,
        "status": 1,
        "paidAt": "2024-01-01T10:00:00",
        "createdAt": "2024-01-01T10:00:00"
      }
    ],
    "total": 5000,
    "page": 1,
    "size": 10
  }
}
```

---

### 评价管理

#### 1. 获取评价列表

**接口**: `GET /api/admin/reviews`

**描述**: 获取所有评价列表

**认证**: 需要 Bearer Token + ADMIN 角色

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）

**响应示例**: 同 `/api/reviews` 的分页响应格式

---

#### 2. 更新评价状态

**接口**: `PUT /api/admin/reviews/{id}/status`

**描述**: 隐藏/显示评价

**认证**: 需要 Bearer Token + ADMIN 角色

**路径参数**:
- `id`: 评价ID

**请求体**:
```json
{
  "status": 1
}
```

**参数说明**:
- `status`: 状态（0-正常，1-隐藏）

**响应示例**:
```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": null
}
```

---

### 投诉管理

#### 1. 获取投诉列表

**接口**: `GET /api/admin/complaints`

**描述**: 获取所有投诉列表

**认证**: 需要 Bearer Token + ADMIN 角色

**查询参数**:
- `page`: 页码（默认1）
- `size`: 每页数量（默认10）
- `status`: 状态筛选（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 1,
        "stallId": 1,
        "orderId": 100,
        "type": 1,
        "content": "菜品质量有问题",
        "images": ["http://example.com/complaint1.jpg"],
        "status": 2,
        "replyContent": "已处理",
        "handlerId": 1,
        "handledAt": "2024-01-02T10:00:00",
        "createdAt": "2024-01-01T10:00:00",
        "updatedAt": "2024-01-02T10:00:00"
      }
    ],
    "total": 50,
    "page": 1,
    "size": 10
  }
}
```

---

#### 2. 处理投诉

**接口**: `PUT /api/admin/complaints/{id}/handle`

**描述**: 处理投诉

**认证**: 需要 Bearer Token + ADMIN 角色

**路径参数**:
- `id`: 投诉ID

**请求体**:
```json
{
  "status": 2,
  "replyContent": "已联系档口处理，已退款"
}
```

**参数说明**:
- `status`: 处理状态（2-已解决，3-已驳回）
- `replyContent`: 处理回复（必填）

**响应示例**:
```json
{
  "code": 200,
  "message": "处理成功",
  "data": null
}
```

---

### 公告管理

#### 1. 创建公告

**接口**: `POST /api/admin/announcements`

**描述**: 创建系统公告

**认证**: 需要 Bearer Token + ADMIN 角色

**请求体**:
```json
{
  "title": "系统维护通知",
  "content": "系统将于今晚22:00-24:00进行维护",
  "type": 0
}
```

**响应示例**: 同获取公告详情

---

#### 2. 更新公告

**接口**: `PUT /api/admin/announcements/{id}`

**描述**: 更新公告信息

**认证**: 需要 Bearer Token + ADMIN 角色

**路径参数**:
- `id`: 公告ID

**请求体**: 同创建公告

**响应示例**: 同获取公告详情

---

#### 3. 删除公告

**接口**: `DELETE /api/admin/announcements/{id}`

**描述**: 删除公告

**认证**: 需要 Bearer Token + ADMIN 角色

**路径参数**:
- `id`: 公告ID

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

#### 4. 发布公告

**接口**: `PUT /api/admin/announcements/{id}/publish`

**描述**: 发布公告

**认证**: 需要 Bearer Token + ADMIN 角色

**路径参数**:
- `id`: 公告ID

**响应示例**:
```json
{
  "code": 200,
  "message": "发布成功",
  "data": null
}
```

---

#### 5. 下架公告

**接口**: `PUT /api/admin/announcements/{id}/takedown`

**描述**: 下架公告

**认证**: 需要 Bearer Token + ADMIN 角色

**路径参数**:
- `id`: 公告ID

**响应示例**:
```json
{
  "code": 200,
  "message": "下架成功",
  "data": null
}
```

---

### 系统配置

#### 1. 获取系统配置列表

**接口**: `GET /api/admin/configs`

**描述**: 获取所有系统配置

**认证**: 需要 Bearer Token + ADMIN 角色

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "configKey": "site.name",
      "configValue": "校园餐饮管理系统",
      "description": "网站名称",
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00"
    }
  ]
}
```

---

#### 2. 更新系统配置

**接口**: `PUT /api/admin/configs`

**描述**: 更新系统配置

**认证**: 需要 Bearer Token + ADMIN 角色

**请求体**:
```json
{
  "key": "site.name",
  "value": "新网站名称"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

### 统计报表

#### 1. 获取仪表盘统计

**接口**: `GET /api/admin/stats/dashboard`

**描述**: 获取管理员仪表盘统计数据

**认证**: 需要 Bearer Token + ADMIN 角色

**响应示例**: 同 `/api/stats/dashboard`

---

#### 2. 获取订单统计

**接口**: `GET /api/admin/stats/orders`

**描述**: 获取订单统计数据

**认证**: 需要 Bearer Token + ADMIN 角色

**查询参数**:
- `startDate`: 开始日期（可选，格式：YYYY-MM-DD）
- `endDate`: 结束日期（可选，格式：YYYY-MM-DD）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "date": "2024-01-01",
      "orderCount": 100,
      "totalAmount": 5000.00
    },
    {
      "date": "2024-01-02",
      "orderCount": 120,
      "totalAmount": 6000.00
    }
  ]
}
```

---

#### 3. 获取菜品排行榜

**接口**: `GET /api/admin/stats/dishes`

**描述**: 获取菜品销售排行榜

**认证**: 需要 Bearer Token + ADMIN 角色

**查询参数**:
- `stallId`: 档口ID（可选）
- `limit`: 返回数量（默认10）

**响应示例**: 同 `/api/stats/dishes/ranking`

---

#### 4. 获取营收统计

**接口**: `GET /api/admin/stats/revenue`

**描述**: 获取营收统计数据

**认证**: 需要 Bearer Token + ADMIN 角色

**查询参数**:
- `stallId`: 档口ID（可选）
- `startDate`: 开始日期（可选，格式：YYYY-MM-DD）
- `endDate`: 结束日期（可选，格式：YYYY-MM-DD）

**响应示例**: 同商户营收统计

---

## 数据模型

### User (sys_user)

用户信息表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 用户ID |
| username | String | 用户名 |
| nickname | String | 昵称 |
| phone | String | 手机号 |
| email | String | 邮箱 |
| avatar | String | 头像URL |
| role | Integer | 角色（0-普通用户，1-商户，2-管理员） |
| status | Integer | 状态（0-禁用，1-正常） |
| balance | BigDecimal | 账户余额 |
| createdAt | LocalDateTime | 创建时间 |
| updatedAt | LocalDateTime | 更新时间 |

---

### Stall

档口信息表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 档口ID |
| merchantId | Long | 商户ID |
| name | String | 档口名称 |
| description | String | 档口描述 |
| logo | String | 档口Logo URL |
| location | String | 位置 |
| phone | String | 联系电话 |
| businessHours | String | 营业时间 |
| status | Integer | 状态（0-待审核，1-营业中，2-休息中，3-已关闭） |
| avgRating | BigDecimal | 平均评分 |
| monthlySales | Integer | 月销量 |
| createdAt | LocalDateTime | 创建时间 |
| updatedAt | LocalDateTime | 更新时间 |

---

### DishCategory

菜品分类表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 分类ID |
| stallId | Long | 档口ID |
| name | String | 分类名称 |
| sortOrder | Integer | 排序顺序 |
| createdAt | LocalDateTime | 创建时间 |
| updatedAt | LocalDateTime | 更新时间 |

---

### Dish

菜品信息表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 菜品ID |
| stallId | Long | 档口ID |
| categoryId | Long | 分类ID |
| name | String | 菜品名称 |
| description | String | 菜品描述 |
| image | String | 菜品图片URL |
| price | BigDecimal | 现价 |
| originalPrice | BigDecimal | 原价 |
| status | Integer | 状态（0-待审核，1-在售，2-下架） |
| stock | Integer | 库存 |
| dailyLimit | Integer | 每日限购 |
| warningThreshold | Integer | 库存预警阈值 |
| monthlySales | Integer | 月销量 |
| rating | BigDecimal | 评分 |
| isRecommended | Boolean | 是否推荐 |
| createdAt | LocalDateTime | 创建时间 |
| updatedAt | LocalDateTime | 更新时间 |

---

### OrderInfo

订单信息表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 订单ID |
| orderNo | String | 订单号 |
| userId | Long | 用户ID |
| stallId | Long | 档口ID |
| totalAmount | BigDecimal | 订单总金额 |
| actualAmount | BigDecimal | 实付金额 |
| status | Integer | 状态（0-待付款，1-已付款，2-制作中，3-待取餐，4-已完成，5-已取消，6-已退款） |
| remark | String | 备注 |
| paymentMethod | Integer | 支付方式（1-余额，2-微信，3-支付宝） |
| paidAt | LocalDateTime | 支付时间 |
| completedAt | LocalDateTime | 完成时间 |
| cancelledAt | LocalDateTime | 取消时间 |
| cancelReason | String | 取消原因 |
| createdAt | LocalDateTime | 创建时间 |
| updatedAt | LocalDateTime | 更新时间 |

---

### OrderItem

订单项表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 订单项ID |
| orderId | Long | 订单ID |
| dishId | Long | 菜品ID |
| dishName | String | 菜品名称（快照） |
| dishImage | String | 菜品图片（快照） |
| price | BigDecimal | 单价（快照） |
| quantity | Integer | 数量 |
| subtotal | BigDecimal | 小计 |
| createdAt | LocalDateTime | 创建时间 |

---

### Payment

支付记录表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 支付ID |
| orderId | Long | 订单ID |
| paymentNo | String | 支付单号 |
| amount | BigDecimal | 支付金额 |
| method | Integer | 支付方式（1-余额，2-微信，3-支付宝） |
| status | Integer | 状态（0-待支付，1-支付成功，2-支付失败，3-已退款） |
| paidAt | LocalDateTime | 支付时间 |
| createdAt | LocalDateTime | 创建时间 |

---

### Review

评价表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 评价ID |
| orderId | Long | 订单ID |
| userId | Long | 用户ID |
| stallId | Long | 档口ID |
| dishId | Long | 菜品ID（可选，为空则为档口评价） |
| rating | Integer | 评分（1-5） |
| content | String | 评价内容 |
| images | String[] | 评价图片数组 |
| replyContent | String | 回复内容 |
| replyAt | LocalDateTime | 回复时间 |
| status | Integer | 状态（0-正常，1-隐藏） |
| createdAt | LocalDateTime | 创建时间 |
| updatedAt | LocalDateTime | 更新时间 |

---

### Complaint

投诉表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 投诉ID |
| userId | Long | 用户ID |
| stallId | Long | 档口ID |
| orderId | Long | 订单ID（可选） |
| type | Integer | 投诉类型（1-食品质量，2-服务态度，3-环境卫生，4-其他） |
| content | String | 投诉内容 |
| images | String[] | 投诉图片数组 |
| status | Integer | 状态（0-待处理，1-处理中，2-已解决，3-已驳回） |
| replyContent | String | 处理回复 |
| handlerId | Long | 处理人ID |
| handledAt | LocalDateTime | 处理时间 |
| createdAt | LocalDateTime | 创建时间 |
| updatedAt | LocalDateTime | 更新时间 |

---

### Announcement

公告表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 公告ID |
| title | String | 标题 |
| content | String | 内容 |
| type | Integer | 类型（0-系统公告，1-商户公告） |
| publisherId | Long | 发布人ID |
| stallId | Long | 档口ID（商户公告时必填） |
| status | Integer | 状态（0-草稿，1-已发布，2-已下架） |
| publishedAt | LocalDateTime | 发布时间 |
| createdAt | LocalDateTime | 创建时间 |
| updatedAt | LocalDateTime | 更新时间 |

---

### Activity

活动表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 活动ID |
| stallId | Long | 档口ID |
| title | String | 活动标题 |
| description | String | 活动描述 |
| image | String | 活动图片URL |
| discountType | Integer | 优惠类型（1-满减，2-折扣，3-赠品） |
| discountValue | BigDecimal | 优惠值 |
| minAmount | BigDecimal | 最低消费金额 |
| startTime | LocalDateTime | 开始时间 |
| endTime | LocalDateTime | 结束时间 |
| status | Integer | 状态（0-未开始，1-进行中，2-已结束） |
| createdAt | LocalDateTime | 创建时间 |
| updatedAt | LocalDateTime | 更新时间 |

---

### CartItem

购物车项表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 购物车项ID |
| userId | Long | 用户ID |
| dishId | Long | 菜品ID |
| stallId | Long | 档口ID |
| quantity | Integer | 数量 |
| createdAt | LocalDateTime | 创建时间 |
| updatedAt | LocalDateTime | 更新时间 |

---

### Favorite

收藏表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 收藏ID |
| userId | Long | 用户ID |
| stallId | Long | 档口ID（type=1时必填） |
| dishId | Long | 菜品ID（type=2时必填） |
| type | Integer | 类型（1-档口，2-菜品） |
| createdAt | LocalDateTime | 创建时间 |

---

### SysConfig

系统配置表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 配置ID |
| configKey | String | 配置键 |
| configValue | String | 配置值 |
| description | String | 描述 |
| createdAt | LocalDateTime | 创建时间 |
| updatedAt | LocalDateTime | 更新时间 |

---

## 测试账号

系统提供了以下测试账号用于开发和测试：

### 管理员账号
- **用户名**: `admin`
- **密码**: `123456`
- **角色**: 管理员（ADMIN）

### 商户账号
- **用户名**: `merchant1` ~ `merchant5`
- **密码**: `123456`
- **角色**: 商户（MERCHANT）

### 普通用户账号
- **用户名**: `user1` ~ `user20`
- **密码**: `123456`
- **角色**: 普通用户（USER）

---

## 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权（未登录或Token过期） |
| 403 | 无权限（角色权限不足） |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 注意事项

1. **认证**: 所有需要认证的接口都必须在请求头中携带 `Authorization: Bearer {token}`，其中 `{token}` 为登录后获取的JWT令牌。

2. **时间格式**: 所有时间字段均使用 ISO 8601 格式：`YYYY-MM-DDTHH:mm:ss`（例如：`2024-01-01T10:00:00`）。

3. **分页参数**: 
   - `page`: 页码，从1开始
   - `size`: 每页数量，默认10

4. **金额单位**: 所有金额字段单位为元（人民币），保留两位小数。

5. **图片URL**: 图片字段存储的是完整的URL地址，需要确保URL可访问。

6. **角色权限**: 
   - 普通用户（role=0）：只能访问用户相关接口和公共接口
   - 商户（role=1）：可以访问商户管理接口
   - 管理员（role=2）：可以访问所有接口

7. **状态枚举**: 各实体的状态字段请参考数据模型说明中的状态值定义。

---

## 更新日志

- **2024-01-01**: 初始版本API文档发布

---

**文档版本**: v1.0  
**最后更新**: 2024-01-01
