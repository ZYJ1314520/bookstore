# 网上书店

基于 Spring Cloud 微服务架构的多角色网上书店系统，支持用户浏览购买、商家入驻管理、平台运营管理。

## 技术栈

**后端**
- Spring Boot 3.2.5 + Spring Cloud 2023.0.1
- Spring Cloud Alibaba 2023.0.1.0（Nacos 注册发现 + Sentinel 限流）
- MyBatis-Plus 3.5.5 + MySQL
- Redis + Redisson 分布式锁
- Spring AI 1.0.0（AI 智能导购）

**前端**
- Vue 3 + Element Plus + Pinia
- Axios + Vue Router

## 微服务架构

| 服务 | 端口 | 说明 |
|------|------|------|
| bookstore-gateway | 8080 | API 网关（路由转发、JWT 鉴权、Sentinel 限流） |
| bookstore-user | 8081 | 用户服务（注册登录、个人中心、商家管理） |
| bookstore-book | 8082 | 图书服务（图书管理、分类、图片、Redis 缓存） |
| bookstore-order | 8083 | 订单服务（下单、支付、评价） |
| bookstore-admin | 8084 | 管理服务（平台看板） |
| bookstore-ai | 8085 | AI 导购服务（Spring AI + MiMo 大模型） |
| bookstore-common | - | 公共模块（实体、DTO、工具类） |

## 三角色体系

- **用户 (role=1)**：浏览图书、加购下单、评价晒单、收藏管理
- **商家 (role=2)**：入驻申请、图书上架、订单处理、数据看板
- **管理员 (role=0)**：平台监控、商家审核、用户管理、图书分类

## 核心功能

- JWT 认证 + Gateway 全局鉴权
- Sentinel 接口限流（Dashboard 可视化配置）
- Redis 缓存热门图书/分类（TTL 5~30 分钟）
- Redisson 分布式锁保护库存操作
- 按商家自动分单
- AI 智能导购对话（悬浮聊天窗口）
- 图片上传与管理

## 快速启动

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.1+

### 1. 数据库

```bash
mysql -u root -p < sql/init.sql
```

### 2. 启动 Nacos

```bash
cd nacos-server-2.1.1/bin
startup.cmd -m standalone
```

### 3. 启动后端微服务

```bash
cd bookstore-cloud
mvn clean package -DskipTest

# 按顺序启动
java -jar bookstore-gateway/target/bookstore-gateway-1.0.0.jar
java -jar bookstore-user/target/bookstore-user-1.0.0.jar
java -jar bookstore-book/target/bookstore-book-1.0.0.jar
java -jar bookstore-order/target/bookstore-order-1.0.0.jar
java -jar bookstore-admin/target/bookstore-admin-1.0.0.jar
java -jar bookstore-ai/target/bookstore-ai-1.0.0.jar
```

或直接运行 `startup.cmd`。

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

访问 http://localhost:3000

### 5. Sentinel 限流（可选）

```bash
java -Dserver.port=8880 -jar sentinel-dashboard-1.8.6.jar
```

访问 http://localhost:8880，账号密码 `sentinel/sentinel`，可对公开接口配置流控规则。

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 用户 | user1 | 123456 |
| 商家 | shop1 | 123456 |

## 项目结构

```
├── bookstore-cloud/          # 微服务后端
│   ├── bookstore-common/     # 公共模块
│   ├── bookstore-gateway/    # API 网关
│   ├── bookstore-user/       # 用户服务
│   ├── bookstore-book/       # 图书服务
│   ├── bookstore-order/      # 订单服务
│   ├── bookstore-admin/      # 管理服务
│   └── bookstore-ai/         # AI 导购服务
├── frontend/                 # Vue 前端
│   ├── src/
│   │   ├── api/              # 接口封装
│   │   ├── components/       # 公共组件
│   │   ├── router/           # 路由配置
│   │   ├── store/            # Pinia 状态管理
│   │   └── views/            # 页面视图
│   └── vite.config.js
├── sql/                      # 数据库脚本
└── docs/                     # 测试文档
```
