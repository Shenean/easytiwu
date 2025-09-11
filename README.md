# EasyTiwu · AI 练题工具（精简版）

一个基于 Spring Cloud 的微服务项目，支持题库上传、练习与 AI 智能解析，前端采用 Vue 3 + TypeScript。

## 项目概览
- 后端：Spring Boot 3、Spring Cloud Gateway、MyBatis、MySQL 8、Apache POI、Lombok、FastJSON2
- AI：阿里云通义千问（DashScope）
- 前端：Vue 3、Vite、Naive UI、Axios、Vue Router、TypeScript
- JDK：17 以上，构建：Maven 3.x

## 模块与端口
- service-gateway（8080）：API 网关
- service-upload（8081）：文件上传与解析
- service-bank（8082）：题库与题目管理
- service-content（8083）：内容管理
- frontend（3000）：前端开发服务器

## 快速开始
1) 准备环境
- JDK 17+、Node.js 18+、MySQL 8+、Maven 3.6+

2) 初始化数据库
- 创建数据库：easytiwu（utf8mb4）
- 执行脚本：DB.SQL（位于项目根目录）

3) 配置必要变量（以系统环境变量或启动参数方式提供）
- DB_PASSWORD：MySQL 密码
- DASHSCOPE_API_KEY：阿里云通义千问 API Key

4) 启动后端（按顺序）
- 进入各服务模块执行：mvn spring-boot:run
  依次启动：service-gateway → service-upload → service-bank → service-content

5) 启动前端
- cd frontend && 安装依赖（npm i 或 pnpm i）
- 运行开发服务器（npm run dev 或 pnpm dev）
- 访问：http://localhost:3000

## 关键配置
- 网关地址：http://localhost:8080
- 数据库：localhost:3306 / easytiwu / 用户名 root / 密码由 DB_PASSWORD 提供
- AI 服务：DashScope（DASHSCOPE_API_KEY）

## 常用 API（经网关转发）
- POST /api/upload/file：上传题库文件
- GET  /api/bank/list：题库列表
- GET  /api/bank/questions：题目查询

## 数据库核心表
- question_banks：题库
- questions：题目（含答案与解析）
- question_options：选择题选项

## 许可证与反馈
- 许可证：MIT（见 LICENSE）
- 反馈：提交 Issue 或发送邮件至项目维护者

—
专注题库管理与高效练习。
