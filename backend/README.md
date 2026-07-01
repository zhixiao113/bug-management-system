# Backend

Spring Boot 3 后端服务，端口 `8081`，接口统一前缀为 `/api`。

## 已实现接口

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/auth/login` | 账号密码登录，返回 token 和用户信息 |
| GET | `/api/auth/me` | 获取当前登录用户 |
| GET | `/api/users` | 获取用户列表，仅管理员可用 |
| GET | `/api/bugs` | Bug 列表，支持 keyword/status/severity/assigneeId 查询 |
| GET | `/api/bugs/{id}` | Bug 详情 |
| POST | `/api/bugs` | 新增 Bug |
| PUT | `/api/bugs/{id}` | 编辑 Bug |
| DELETE | `/api/bugs/{id}` | 删除 Bug，仅管理员可用 |
| PUT | `/api/bugs/{id}/status` | 修改 Bug 状态 |
| PUT | `/api/bugs/{id}/assign` | 转交处理人 |
| GET | `/api/bugs/{id}/logs` | 查看操作记录 |
| GET | `/api/statistics/overview` | 查看统计数据 |

## 本地构建

```bash
mvn -q -DskipTests package
```

## 登录账号

| 角色 | 用户名 | 密码 |
|---|---|---|
| 管理员 | admin | 123456 |
| 测试人员 | tester | 123456 |
| 开发人员 | developer | 123456 |
