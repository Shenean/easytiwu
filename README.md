# EasyTiwu AI练题工具

一个基于Spring Cloud微服务架构的智能AI练题工具，支持题库上传、题目练习、AI智能解析等功能。

## 🚀 项目特性

- **微服务架构**: 基于Spring Cloud Gateway的分布式架构
- **智能解析**: 集成阿里云通义千问大模型，智能解析题目内容
- **文件上传**: 支持Excel、Word等格式的题库文件上传
- **题库管理**: 完整的题库CRUD操作和统计功能
- **现代化前端**: Vue 3 + TypeScript + Naive UI的响应式界面
- **数据持久化**: MySQL数据库存储，支持事务和触发器

## 🏗️ 系统架构

### 后端服务

- **service-gateway** (8080): API网关，负责路由转发和跨域处理
- **service-upload** (8081): 文件上传服务，处理题库文件解析和AI分析
- **service-bank** (8082): 题库管理服务，提供题库和题目的CRUD操作
- **service-content** (8083): 内容管理服务

### 前端应用

- **frontend**: Vue 3 + TypeScript + Vite构建的现代化Web应用

### 公共模块

- **common-exception**: 统一异常处理模块
- **starter-db**: 数据库配置和MyBatis集成
- **BOM**: 依赖版本管理

## 🛠️ 技术栈

### 后端技术

- **框架**: Spring Boot 3.x, Spring Cloud Gateway
- **数据库**: MySQL 8.0, MyBatis
- **AI集成**: 阿里云通义千问 (DashScope)
- **文件处理**: Apache POI
- **工具库**: Lombok, FastJSON2
- **构建工具**: Maven 3.x
- **JDK版本**: Java 17

### 前端技术

- **框架**: Vue 3.5.x
- **语言**: TypeScript
- **构建工具**: Vite 7.x
- **UI组件**: Naive UI 2.42.x
- **路由**: Vue Router 4.x
- **HTTP客户端**: Axios
- **工具库**: @vueuse/core

## 📦 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.6+

### 数据库初始化

1. 创建数据库:
```sql
CREATE DATABASE easytiwu CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行数据库脚本:
```bash
mysql -u root -p easytiwu < DB.SQL
```

### 后端启动

1. 克隆项目:
```bash
git clone <repository-url>
cd easytiwu
```

2. 配置环境变量:
```bash
# 数据库密码
export DB_PASSWORD=your_mysql_password

# 阿里云通义千问API Key
export DASHSCOPE_API_KEY=your_dashscope_api_key
```

3. 编译项目:
```bash
mvn clean compile
```

4. 启动服务 (按顺序):
```bash
# 1. 启动网关服务
cd service-gateway
mvn spring-boot:run

# 2. 启动上传服务
cd ../service-upload
mvn spring-boot:run

# 3. 启动题库服务
cd ../service-bank
mvn spring-boot:run

# 4. 启动内容服务
cd ../service-content
mvn spring-boot:run
```

### 前端启动

1. 安装依赖:
```bash
cd frontend
npm install
# 或使用 pnpm
pnpm install
```

2. 启动开发服务器:
```bash
npm run dev
# 或
pnpm dev
```

3. 访问应用:
```
http://localhost:3000
```

## 🔧 配置说明

### 服务端口配置

| 服务 | 端口 | 说明 |
|------|------|------|
| service-gateway | 8080 | API网关 |
| service-upload | 8081 | 文件上传服务 |
| service-bank | 8082 | 题库管理服务 |
| service-content | 8083 | 内容管理服务 |
| frontend | 3000 | 前端开发服务器 |

### 数据库配置

默认数据库连接配置:
- **地址**: localhost:3306
- **数据库**: easytiwu
- **用户名**: root
- **密码**: 通过环境变量 `DB_PASSWORD` 配置

### AI服务配置

- **服务商**: 阿里云通义千问
- **API Key**: 通过环境变量 `DASHSCOPE_API_KEY` 配置
- **功能**: 智能题目解析和内容生成

## 📚 API文档

### 主要接口

- **文件上传**: `POST /api/upload/file`
- **题库列表**: `GET /api/bank/list`
- **题目查询**: `GET /api/bank/questions`
- **内容管理**: `GET /api/content/*`

所有API请求通过网关 (8080端口) 进行路由转发。

## 🗃️ 数据库设计

### 核心表结构

- **question_banks**: 题库表，存储题库基本信息和统计数据
- **questions**: 题目表，存储题目内容、答案、解析等
- **question_options**: 选项表，存储选择题的选项内容

### 特性

- 支持多种题型：单选、多选、填空、判断、简答
- 自动统计功能：总题数、完成数、错题数
- 数据库触发器：自动维护统计数据一致性

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系方式

如有问题或建议，请通过以下方式联系:

- 提交 Issue
- 发送邮件至项目维护者

---

**EasyTiwu** - 让题库管理更简单，让学习更高效！
