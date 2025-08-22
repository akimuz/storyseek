# StorySeek Backend

> **StorySeek** 是一个面向中长篇小说创作的 AI 辅助平台，后端基于 **Spring Boot 3.4.6** 构建，包括 **角色设定、世界观、大纲、细纲与正文生成** 等功能，并集成提示词生态。

---

## 功能特性

- **写作模块**：支持书籍、卷、章节的创建、管理与正文编辑。
- **角色与世界观**：提供角色库与设定管理，支持插入编辑器。
- **AI 生成**：
  - 大纲、细纲、正文生成
  - 流式输出 (SSE) 接口
  - 提示词管理与收藏
- **系统支持**：
  - 用户注册 / 登录（邮箱验证）
  - 权限与鉴权（Sa-Token）
  - 日志系统（控制台 + 定时持久化数据库）
  - 监控指标（Micrometer + Prometheus + Grafana）

---

## 技术栈

- **框架**：Spring Boot 3.4.6
- **安全**：Sa-Token
- **数据库**：MySQL 8.0
- **监控**：Spring Actuator + Micrometer + Prometheus + Grafana
- **依赖管理**：Maven

---

## 项目结构
```md
storyseek-backend
└── src
    └── main
        ├── java/cn/timflux/storyseek
        │   ├── StoryseekApplication.java
        │   ├── ai
        │   │   ├── annotation          <!-- 模型提供者注解 -->
        │   │   ├── model               <!-- AI 模型接口与实现 -->
        │   │   └── service             <!-- AI 生成服务 -->
        │   ├── common
        │   │   ├── api                 <!-- ApiResponse 等通用封装 -->
        │   │   ├── config              <!-- Web/安全/Mybatis配置 -->
        │   │   ├── exception           <!-- 全局异常处理 -->
        │   │   ├── logging             <!-- 日志实体与Mapper -->
        │   │   ├── metrics             <!-- Prometheus指标 & AOP埋点 -->
        │   │   └── util                <!-- 工具类（PingController 等） -->
        │   ├── core
        │   │   ├── browsing            <!-- 浏览相关逻辑 -->
        │   │   ├── story               <!-- 小说业务模块 -->
        │   │   ├── user                <!-- 用户管理 -->
        │   │   └── write
        │   │       ├── edit            <!-- 编辑相关 Controller/Service/DTO/Entity/Mapper -->
        │   │       └── promptsea       <!-- 提示词海模块 -->
        │   └── strategy                <!-- 各类写作策略 -->
        └── resources
            ├── application.properties
            ├── credentials             <!-- 服务账号凭证 -->
            ├── logback-spring.xml      <!-- 日志配置 -->
            └── mapper                  <!-- MyBatis XML -->

