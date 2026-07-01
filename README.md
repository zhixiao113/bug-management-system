# 缺陷管理系统 / Bug 管理系统

学校期末作业项目：一个前后端分离的缺陷管理网页系统。系统支持账号登录、角色权限、Bug 管理、状态流转、转交处理人、操作记录、统计图表，并提供 Docker Compose 一键启动能力。

## 技术栈

| 模块 | 技术 |
|---|---|
| 前端 | Vue 3、Vite、Element Plus、Vue Router、Axios、ECharts |
| 后端 | Spring Boot 3、Spring Web、JDBC Template |
| 数据库 | MySQL 8 |
| 部署 | Docker、Docker Compose、Nginx |
| 目标环境 | openEuler / ARM aarch64 / 鲲鹏云服务器 |

## 目录结构

```text
bug-management-system/
├── backend/              # Spring Boot 后端
├── frontend/             # Vue 3 前端
├── mysql/init.sql        # MySQL 建表和初始化数据
├── docs/                 # 项目说明、部署说明、截图清单
├── docker-compose.yml    # 一键启动编排
└── README.md
```

## 核心功能

- 账号密码登录，接口请求携带 token
- 管理员、测试人员、开发人员三种角色
- 按角色展示不同菜单和操作按钮
- 新增、编辑、删除、查询 Bug
- 按标题、状态、严重程度筛选 Bug
- Bug 状态流转：已提交 → 已修复 → 已关闭
- Bug 转交处理人
- 操作记录留痕
- 控制台统计卡片和 ECharts 图表
- Docker 容器化部署

## 测试账号

| 角色 | 用户名 | 密码 |
|---|---|---|
| 管理员 | admin | 123456 |
| 测试人员 | tester | 123456 |
| 开发人员 | developer | 123456 |

## Docker 一键启动

确保 Docker 和 Docker Compose 可用后，在项目根目录执行：

```bash
docker compose up -d --build
```

启动后访问：

```text
http://localhost:8080
```

容器端口：

| 服务 | 容器端口 | 宿主机端口 |
|---|---:|---:|
| frontend | 80 | 8080 |
| backend | 8081 | 8081 |
| mysql | 3306 | 3306 |

前端 Nginx 代理：

```text
/api -> backend:8081
```

## 常用命令

```bash
docker compose ps
docker compose logs -f
docker compose down
```

## 本地开发

后端：

```bash
cd backend
mvn spring-boot:run
```

前端：

```bash
cd frontend
npm install
npm run dev
```

开发环境访问：

```text
http://localhost:5173
```

## 文档

- [项目说明](docs/项目说明.md)
- [部署说明](docs/部署说明.md)
- [截图清单](docs/截图清单.md)
