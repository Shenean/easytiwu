# EasyTiwu · AI 练题工具

基于 Spring Cloud 微服务架构的智能练题平台，支持题库文件上传、在线练习和 AI 智能解析。

## 技术栈

**后端**
- Spring Boot 3.3.13 + Spring Cloud Gateway
- MyBatis Plus + MySQL 8
- Apache POI（文档解析）+ 阿里云通义千问（AI 解析）

**前端**
- Vue 3 + TypeScript + Vite
- Naive UI + Vue Router + Pinia

**环境要求**
- JDK 17+、Node.js 18+、MySQL 8+、Maven 3.6+

## 服务架构

| 服务               | 端口 | 功能                   |
|--------------------|------|------------------------|
| frontend           | 3000 | 前端开发服务器         |
| service-gateway    | 8080 | API 网关，统一路由转发 |
| service-bank       | 8081 | 题库与题目管理         |
| service-content    | 8082 | 内容管理服务           |
| service-statistics | 8083 | 数据统计服务           |
| service-upload     | 8084 | 文件上传与题库解析     |


## 快速启动

### 1. 数据库初始化
```sql
-- 创建数据库
CREATE DATABASE easytiwu CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 执行建表脚本
source DB.SQL
```

### 2. 环境配置
配置以下环境变量或在 application.yml 中设置：
```bash
DB_PASSWORD=your_mysql_password
DASHSCOPE_API_KEY=your_dashscope_api_key(仅upload模块)
```

### 3. 启动服务
**后端服务**（按顺序启动）
```bash
# 1. 启动网关
cd service-gateway && mvn spring-boot:run

# 2. 启动上传服务
cd service-upload && mvn spring-boot:run

# 3. 启动题库服务
cd service-bank && mvn spring-boot:run

# 4. 启动内容服务
cd service-content && mvn spring-boot:run

# 5. 启动统计服务
cd service-statistics && mvn spring-boot:run
```

**前端服务**
```bash
cd frontend
npm install
npm run dev
```

### 4. 访问应用
- 前端地址：http://localhost:3000
- API 网关：http://localhost:8080

## 核心功能

- **题库管理**：支持 Excel、Word、PDF 格式题库文件上传
- **智能解析**：基于阿里云通义千问的题目自动解析
- **在线练习**：支持单选、多选、填空、判断、简答等题型
- **统计分析**：练习进度、正确率、错题统计


## 数据库设计

- `question_banks`：题库信息表
- `questions`：题目详情表（含答案解析）
- `question_options`：选择题选项表

## 开发说明

- 项目采用 Maven 多模块管理
- 统一依赖版本通过 BOM 模块管理
- 数据库连接池使用 HikariCP
- 前端代理配置自动转发到网关服务

---

**License**: MIT | **反馈**: 提交 Issue 或 PR
