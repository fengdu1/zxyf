# 智学云帆-教学管理系统

基于 **Spring Boot + MyBatis + PageHelper** 的员工管理与登录系统，附带 Vue 3 前端。

## 技术栈

| 模块 | 技术 | 版本 |
|---|---|---|
| 框架 | Spring Boot（webmvc） | 4.1.0 |
| ORM | MyBatis（注解式 SQL，无 XML） | mybatis-spring-boot-starter 4.1.0 |
| 分页 | PageHelper | 6.1.0 |
| 数据库 | MySQL（JDBC 驱动 mysql-connector-j，由 Boot BOM 管理） | 9.7.x |
| 鉴权 | 手写 HS256 JWT（无第三方依赖） | - |
| 文件存储 | 阿里云 OSS | aliyun-sdk-oss 3.18.2 |
| 语言 | Java（record / 文本块等新语法） | 26 |
| 前端 | Vue 3 + Vite + TypeScript | - |

## 目录结构

```
demo/
├── src/main/java/com/feng/demo/
│   ├── common/        # 统一响应 Result
│   ├── config/        # WebConfig(拦截器注册)、OssProperties、MybatisConfig
│   ├── controller/    # Emp、EmpExpr、Dept、Login、Upload 控制器
│   ├── dto/           # 入参对象（record）
│   ├── interceptor/   # LoginInterceptor（token 校验）
│   ├── mapper/        # EmpMapper、EmpExprMapper、DeptMapper（注解 SQL）
│   ├── pojo/          # 实体（Emp 为 @Data，Dept/EmpExpr 为 record）
│   ├── service/       # 业务接口与实现
│   ├── utils/         # JwtUtils
│   └── vo/            # 出参对象
├── src/main/resources/application.yaml   # 主配置（密钥已脱敏为 ${} 占位符）
├── secrets.yaml       # 本地敏感配置（已被 .gitignore 忽略，勿提交）
└── frontend/          # Vue3 + Vite 前端
```

## 快速开始

### 1. 准备数据库

执行以下 DDL 建表：`dept`（部门）、`emp`（员工）、`emp_expr`（工作经历）。

`emp` 除文档标准字段外，额外含 `original_name`（OSS 原始文件名，用于下载时还原文件名）：

```sql
ALTER TABLE emp ADD COLUMN original_name VARCHAR(255) COMMENT 'OSS 原始文件名' AFTER image;
```

### 2. 配置密钥（secrets.yaml）

仓库内密钥已脱敏，`application.yaml` 通过 `spring.config.import: optional:file:./secrets.yaml` 引入占位符。克隆后需在项目根目录创建 `secrets.yaml`：

```yaml
ZXYF_DB_PASSWORD: <数据库密码>
ZXYF_JWT_SECRET: <JWT签名密钥>
ZXYF_OSS_ACCESS_KEY_ID: <OSS AccessKeyId>
ZXYF_OSS_ACCESS_KEY_SECRET: <OSS AccessKeySecret>
```

也可改为同名环境变量注入。

### 3. 启动后端

```bash
# Windows
mvnw.cmd spring-boot:run
# 或打包
mvnw.cmd install -DskipTests
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

默认端口 **8090**。

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

开发环境 Vite 代理 `/api` → `http://localhost:8090`（见 `frontend/vite.config.ts`），生产环境由 nginx 反向代理。

## 接口一览

基础响应格式：`{"code":1,"msg":"success","data":...}`，`code=0` 表示业务失败。

### 登录鉴权

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/login` | 登录，成功下发 JWT（data: id/username/name/token），失败返回"用户名或密码错误" |

除 `/login` 外所有接口需在请求头携带 `token: <JWT>`，未登录返回 **401**。

### 员工管理

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/emps` | 分页条件查询（name/gender/begin/end/page/pageSize），含 deptName，不含 password |
| GET | `/emps/{id}` | 详情，含 exprList 与 originalName |
| GET | `/emps/list` | 全部员工（含 password） |
| POST | `/emps` | 新增（含 exprList 批量插入，密码默认 123456） |
| PUT | `/emps` | 修改（经历先删后插） |
| DELETE | `/emps?ids=1,2,3` | 批量删除（级联删经历） |

新增/修改请求体中 `image` 存 OSS URL，`originalName` 存原始文件名。

### 工作经历（emp_expr）

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/exprs?empId=1` | 按员工查经历 |
| POST | `/exprs` | 新增单条 |
| PUT | `/exprs` | 修改单条 |
| DELETE | `/exprs/{id}` | 删除单条 |

### 文件上传/下载

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/upload` | multipart 上传（参数名 `file`），OSS 以 UUID 命名保留原扩展名，返回 `data` 为文件 URL |
| GET | `/file/download?url=...` | 下载；先查 `emp.original_name` 作为 `Content-Disposition` 文件名（未命中则用 UUID 名） |

> 依赖说明：上传文件与员工记录是两步操作，前端需把上传返回的 URL 存入 `image`、原始文件名存入 `originalName` 后随新增/修改员工一起提交。

## 测试

`src/test/java` 下含 11 个 MockMvc 控制器测试（覆盖员工/经历接口），测试连接配置指向的数据库并自动清理数据。

> 注意：测试依赖目标库可用。若目标库不稳定，打包时可用 `mvnw.cmd install -DskipTests` 跳过测试。

## 部署

- 后端：`java -jar` 或 Docker
- 前端：`npm run build` 产出 `dist/`，由 nginx 托管并反向代理 `/api` 到后端 8090
- nginx 需注意：`client_max_body_size`（上传限制，默认 1m 会 413）、透传 `token` 请求头
