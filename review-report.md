# 健身房管理系统 — 全面 Review

---

## 维度一：代码质量 Review

### 一、前端（Vue 3 + Element Plus）

#### 1.1 组件结构与代码复用

**问题：登录页存在大量重复代码**

[AdminLogin.vue](源码/frontend/src/pages/AdminLogin.vue) 与 [UserLogin.vue](源码/frontend/src/pages/UserLogin.vue) 的模板、脚本逻辑、样式相似度超过 90%。两个文件各 450+ 行，差异仅在于：
- 表单字段名（`adminAccount` vs `memberAccount`）
- API 端点
- 左侧插画区的 SVG

应抽取为一个 `<LoginPage role="admin|user">` 组件，通过 prop 区分角色，消除 ~400 行重复代码。

**问题：表单验证使用 `alert()` 而非 Element Plus 的 Form Validation**

[MemberToAddMember.vue:60-66](源码/frontend/src/pages/MemberToAddMember.vue#L60-L66)、[ClassToAddClass.vue:79-81](源码/frontend/src/pages/ClassToAddClass.vue#L79-L81) 等添加表单全部使用 `alert()` 做校验：

```js
if (!form.memberName) return alert('请输入姓名！')
```

应使用 `el-form` 的 `:rules` 属性配合 `el-form-item` 的 `prop` 做声明式校验，用户体验和代码可维护性都更好。

**问题：内联样式过多**

[UserMain.vue](源码/frontend/src/pages/UserMain.vue) 和 [UserToApplyClass.vue](源码/frontend/src/pages/UserToApplyClass.vue) 大量使用 `style="..."` 内联样式。应提取为 scoped CSS class。

**问题：TypeScript 使用不一致**

部分组件使用 `<script setup lang="ts">`（如 ClassToAddClass.vue），部分只写 `<script setup>`（如 MemberToAddMember.vue）。类型声明不统一。

#### 1.2 命名规范

**问题：路由与 API 路径语义混乱**

路由路径如 `/member/toAddMember`、`/class/selClass` 中的 `to`/`sel` 前缀暴露了后端 RPC 风格，不符合前端路由命名惯例。建议改为 `/member/add`、`/class/list` 等语义清晰的路径。

**问题：`postForm` 命名不直观**

[client.ts:8](源码/frontend/src/api/client.ts#L8) 中 `postForm` 实际上是"用 URL-encoded 格式发 POST 请求"，名称暗示是表单提交，但任意数据都用它发送。

#### 1.3 其他问题

- **`router/index.ts:42`** — `verifiedRole` 缓存机制在用户登出后可能不刷新，导致已登出用户绕过后端校验直接进入页面（虽然 API 层有拦截器兜底，但前端状态不一致）。
- **删除确认弹窗** — [MemberSelMember.vue:153](源码/frontend/src/pages/MemberSelMember.vue#L153) 使用浏览器原生 `confirm()`，应换为 `ElMessageBox.confirm` 保持 UI 一致。
- **缺少统一错误处理** — 每个页面各自 `catch(() => {})` 吞掉错误，没有全局 toast/notification 提示用户。

---

### 二、后端接口

#### 2.1 RESTful 风格 —— 严重不符合规范

当前所有 API 都是 RPC 风格（动词+名词）：

| 当前 URL | HTTP 方法 | 问题 | 建议 |
|---|---|---|---|
| `GET /api/member/selMember` | GET | `sel` 前缀多余 | `GET /api/members` |
| `POST /api/member/addMember` | POST | `add` 动词冗余，POST 本身就表示创建 | `POST /api/members` |
| `POST /api/member/delMember` | POST | **删除应使用 DELETE** | `DELETE /api/members/{id}` |
| `GET /api/member/toUpdateMember` | GET | `to` 前缀无意义 | `GET /api/members/{id}` |
| `POST /api/member/updateMember` | POST | 应使用 PUT/PATCH | `PUT /api/members/{id}` |
| `GET /api/member/toSelByCard` | GET | 空返回 | 直接复用 `GET /api/members?account=xxx` |
| `GET /api/member/search` | GET | 语义和 `/selMember` 重叠 | 合并为 `GET /api/members?keyword=&gender=...` |

以上问题在 `ApiEmployeeController`、`ApiEquipmentController`、`ApiClassController` 中全部同样存在。整个后端 API 设计需要统一重构为资源导向的 RESTful 风格。

#### 2.2 错误处理

**问题：缺少全局异常处理器**

项目没有 `@ControllerAdvice`，所有未捕获异常会返回 Spring Boot 默认的 500 JSON（含 stack trace）。应添加全局异常处理：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handle(Exception e) {
        // 返回统一的 {success: false, message: ...}
    }
}
```

**问题：事务缺失**

[ApiClassController.java:103-108](源码/gym-management-system/src/main/java/com/gym/controller/ApiClassController.java#L103-L108) 删除课程时连续调用两个 service：

```java
classTableService.deleteClassByClassId(classId);   // 删课程
classTableService.deleteRecordByClassId(classId);   // 删关联记录
```

如果第二步失败，第一步已提交。应在 Service 层加 `@Transactional`。

**问题：无输入校验**

Controller 方法参数没有 `@Valid` 注解，POJO 没有 `@NotNull`/`@NotBlank` 等约束。用户可以提交空的课程名称、非法的价格等。

#### 2.3 代码风格不一致

- `ApiLoginController` 使用**构造器注入**（推荐），其余 6 个 Controller 全部使用 `@Autowired` **字段注入**（不便于单元测试）。
- `ClassTableServiceImpl.deleteRecordByClassId()` 方法逻辑上属于 `ClassRecordService`，放在了错误的服务类中。

#### 2.4 随机 ID 生成不可靠

[ApiMemberController.java:57-63](源码/gym-management-system/src/main/java/com/gym/controller/ApiMemberController.java#L57-L63)：

```java
String account1 = "2021";
for (int i = 0; i < 5; i++) {
    account1 += random.nextInt(10);
}
```

这种字符串拼接生成 ID 的方式：（1）存在碰撞可能；（2）在并发下不安全；（3）与数据库 `AUTO_INCREMENT` 语义冲突。应直接让数据库生成主键，插入后通过 `useGeneratedKeys` 获取。

#### 2.5 密码明文存储

[gym_management_system.sql:17](源码/gym_management_system.sql#L17) 中所有用户密码均为 `'123456'` 明文。即使作为课设，也建议使用 BCrypt 哈希，并在实验报告中说明。

---

### 三、数据库 SQL

#### 3.1 DDL 文件与应用代码不一致

提供的 [gym_management_system.sql](源码/gym_management_system.sql) 与实际运行的 MyBatis XML 存在显著差异：

| 对比项 | DDL 列名 | MyBatis XML 中引用的列名 |
|---|---|---|
| 会员主键 | `member_account` | `member_id` |
| 课程表 | 无 `class_level`, `class_duration`, `coach_id`, `max_capacity`, `class_description`, `price` 列 | 引用了这些列 |
| 报名表 | 表名 `class_order`（列 `member_account`, `member_name`, `class_name`, `coach`） | 表名 `class_record`（列 `member_id`, `status`, `rating`, `total_sessions`） |
| 器材关联 | 未定义 | 引用了 `class_equipment` 表 |

**结论：提供的 SQL 文件与实际运行数据库的 schema 是两个不同版本。** 需要在实验报告中放正确版本的完整 DDL。

#### 3.2 索引缺失严重

当前所有表只有主键索引。以下查询场景缺少必要的索引：

- `member_name LIKE '%keyword%'` → 全表扫描（需要一个 FULLTEXT 索引，或至少普通索引用于前缀匹配）
- `class_record(member_id)` → 高频 JOIN 列，无索引
- `class_record(class_id)` → JOIN 列，无索引
- `class_record(rating)` → 用于 BETWEEN 查询和 GROUP BY，无索引
- `employee(position)` → 搜索过滤列，无索引

#### 3.3 数据类型不合理

- `class_begin` 用 `varchar(255)` 存日期时间 → 应用 `DATETIME`
- `class_time`（课程时长）用 `varchar(255)` 存 "60分钟" → 应用 `INT`（分钟数），展示层拼接单位
- 手机号用 `bigint` → 应用 `varchar(20)`，避免溢出和丢失前导零

#### 3.4 无外键约束、无级联删除

所有表之间没有 `FOREIGN KEY`。删除会员后，`class_order` 中的报名记录成为孤立数据。建议至少添加外键并在报告中论述为何使用/不使用。

#### 3.5 `class_order` 表存在数据冗余

`class_name`、`coach`、`member_name`、`class_begin` 都可以从 `class_table`、`member`、`employee` 表中 JOIN 获取。这种非规范化设计会导致数据不一致（例如教练改名后，历史记录显示旧名字）。

#### 3.6 ExperimentMapper XML 中的 SQL 问题

- **硬编码 ID**：[ExperimentMapper.xml:12](源码/gym-management-system/src/main/resources/mapper/ExperimentMapper.xml#L12) 中 `WHERE cr.class_id = 1` 硬编码瑜伽课 ID，多个查询均有此问题
- **查询5 逻辑问题**：[ExperimentMapper.xml:47-L58](源码/gym-management-system/src/main/resources/mapper/ExperimentMapper.xml#L47-L58) —— "查询所有学生除了选修1号课程外所有成绩均及格的学生的学号和平均成绩"：当前实现排除了有不及格记录的学生，但题目要求是"除1号课外成绩均及格"，逻辑不完全一致（如果学生在1号课不及格，不应被排除）
- **查询7** 引用了 `ct.class_level = 3`，但 DDL 中无此列

---

## 维度二：数据库课设要求对照

对照 [数据库原理课程实验 2026 内容与要求](数据库原理课程实验%2026%20内容与要求.md)，逐项检查：

### 实验一：SQL定义功能、数据插入（0.5学时）

| 要求 | 完成情况 |
|---|---|
| 建立基本表 | 已提供 [gym_management_system.sql](源码/gym_management_system.sql)，包含 admin、member、employee、equipment、class_table、class_order 六张表 |
| DROP TABLE | 有（每个 CREATE TABLE 前有 `DROP TABLE IF EXISTS`） |
| ALTER TABLE | **未体现** — 实验报告需要演示 |
| CREATE INDEX | **未体现** — 代码中无任何索引创建语句 |
| DROP INDEX | **未体现** |
| INSERT | 有（每个表有初始数据） |

### 实验二：数据查询（2学时）

ExperimentController 实现了 9 组查询，映射到课程要求：

| 课程要求 | 对应实现 | 状态 |
|---|---|---|
| (1) 查询选修1号课程的学生学号与姓名 | query1/query2 | **部分完成**（查询了瑜伽/增肌课，但不是用参数化的课程号） |
| (2) 查询选修课程名为"数据结构"的学生 | query2（按课程名查询增肌课） | 结构对了，但课程名硬编码 |
| (3) 查询不选1号课程的学生 | query3（NOT IN） | 完成 |
| (4) 查询学习全部课程学生姓名 | query4（双重 NOT EXISTS 除法） | 完成 |
| (5) 除1号课外成绩均及格的学号和平均成绩 | query5 | **逻辑有偏差**（见上文 3.6） |
| (6) 查询数据库原理成绩第2名 | query6（LIMIT 1 OFFSET 1） | 完成 |
| (7) 3学分课程3门以上80分 | query7 | SQL 正确，但依赖 `class_level` 列替代"学分"概念 |
| (8) 选课门数唯一的学生 | query8 | 完成 |
| (9) SELECT各种查询条件 | query9a-9g（LIKE/BETWEEN/IN/IS NULL/DISTINCT/ORDER BY/GROUP BY+HAVING） | 完成 |

### 实验三：数据修改、删除（0.5学时）

| 要求 | 完成情况 |
|---|---|
| 1. 把1号课程非空成绩提高10% | **未实现** — ExperimentController 中没有对应的 UPDATE 端点 |
| 2. 删除"数据结构"课程的成绩元组 | **未实现** |
| 3. 删除学号为202415122的所有数据 | **未实现**（应用有通用删除功能，但不是题目要求的具体操作） |

需要补充这三个特定的 SQL 操作到 ExperimentController 中。

### 实验四：视图的操作（0.5学时）

| 要求 | 完成情况 |
|---|---|
| 1. 建立男学生视图（学号、姓名、选修课程名、成绩） | **未实现** — SQL 文件中无 `CREATE VIEW` |
| 2. 在视图中查询平均成绩>80的学生 | **未实现** |

**这是最大的缺失项之一。** 需要创建视图并在 ExperimentController 中提供对应查询。

### 实验五：库函数、授权控制（0.5学时）

| 要求 | 完成情况 |
|---|---|
| 1. 计算每个学生有成绩的课程门数、平均成绩 | **部分完成**（query9g 算课程平均分，但没有按学生维度聚合） |
| 2. GRANT 授权 | **未实现** |
| 3. 撤销基本表和视图 | **未实现** |

### 实验六：综合实验（12学时）— 主体项目

| 要求 | 完成情况 |
|---|---|
| 数据增加、删除、修改、查询 | 已实现（会员、员工、器材、课程的完整 CRUD） |
| 较多查询功能 | 已实现（多维搜索、课程分析、报名记录查询） |
| 集成实验一~五功能一半以上 | **未达标**（实验三~五的大部分功能缺失） |
| 用户界面友好 | 已做到（Vue 3 + Element Plus，登录页有设计感） |

### 加分项评估

| 加分项 | 状态 |
|---|---|
| (1) 100万条记录+性能测试+优化方案 | **未实现** — 无数据生成脚本，无性能测试 |
| (2) 界面功能强大，达到接近现实应用 | **有潜力** — 课程分析（容量利用率进度条、评分统计）做得不错，但缺少数据可视化图表 |
| (3) 触发器、存储过程、游标、完整性命名子句 | **未实现** |
| (4) 其他数据库相关技术功能 | **未实现** |

---

## 总结与改进建议（按优先级排序）

### 高优先级（必做，影响课设验收）

1. **补齐实验三~五**：在 ExperimentController 中添加 UPDATE（成绩提高10%）、DELETE、CREATE VIEW、GRANT/REVOKE 对应的 SQL 和端点
2. **同步 DDL 文件**：确保提交的 SQL 文件与实际运行库的 schema 完全一致（包括 class_record 表、class_equipment 表，以及 class_table 的所有新增列）
3. **创建索引**：至少为 `class_record(member_id)`、`class_record(class_id)` 和 `member(name)` 添加索引；在实验报告中分析执行计划
4. **添加外键约束** 或在报告中说明为何不使用
5. **使用视图**：至少创建"男学生选课成绩视图"以满足实验四要求

### 中优先级（代码质量提升）

6. **重构 RESTful API**：将路径改为资源导向风格（`GET /api/members`、`DELETE /api/members/{id}` 等）
7. **提取登录公共组件**：消除 AdminLogin 与 UserLogin 间的重复代码
8. **添加 `@Transactional`**：特别是删除课程时
9. **使用 `@Valid` 做输入校验**
10. **统一依赖注入方式**：全部改为构造器注入
11. **替换 `alert()` 和 `confirm()`** 为 Element Plus 的 ElMessage / ElMessageBox

### 低优先级（锦上添花，争取加分）

12. **存储过程 + 触发器**：例如创建触发器在插入 class_record 时自动扣减会员剩余课时
13. **生成 100 万条测试数据** 并分析加了索引前后的查询耗时，写入实验报告作为加分项(1)
14. **数据可视化**：在课程分析页用 ECharts 画柱状图/饼图展示课程报名分布，争取加分项(2)
15. **密码哈希**：即使课设不要求安全，也是个好的论述点
