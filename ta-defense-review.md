# 健身房管理系统 — 助教视角答辩 Review

> 模拟老师验收时的检查思路，帮你提前发现会被追问的问题，并给出应对建议。

---

## 第一块：数据库设计（老师会打开你的 MySQL 看）

### 1.1 你设计了几张表？分别干什么？

当前实际运行的 schema 有 **7 张表**：

| 表名 | 用途 | 记录数 |
|---|---|---|
| `admin` | 管理员登录账号 | 3 |
| `member` | 会员信息（账号、姓名、性别、年龄、电话、课时） | 12 |
| `employee` | 员工信息（账号、姓名、性别、年龄、入职时间、职务） | 6 |
| `class_table` | 课程信息（名称、时间、时长、等级、教练、容量、价格） | 11 |
| `class_record` | 会员选课报名记录（会员ID、课程ID、报名时间、状态、评分） | 26 |
| `equipment` | 器材信息（名称、位置、状态） | 7 |
| `class_equipment` | 课程-器材多对多关联表 | 待补充 |

**老师可能会问的追问：**

- "为什么 member 和 employee 不合并成一张 user 表？" → 答案：两类用户业务字段不同。会员有课时、体重、身高；员工有职务、入职时间。分开设计符合各自业务边界。
- "class_equipment 为什么单独一张表？" → 答案：课程和器材是多对多关系（一门课用多种器材，一种器材被多门课用），这是标准的关系模型做法。

### 1.2 有没有索引？

**当前状态：只有主键索引，没有创建任何辅助索引。**

这是你答辩时会被扣分的地方。**老师会直接问："你的搜索功能用到了哪些索引？"**

建议答辩前至少补上 3 个索引，并把执行计划截图放进实验报告：

```sql
-- 高频 JOIN 列
CREATE INDEX idx_cr_member ON class_record(member_id);
CREATE INDEX idx_cr_class ON class_record(class_id);

-- 搜索列
CREATE INDEX idx_member_name ON member(member_name);
CREATE INDEX idx_equipment_name ON equipment(equipment_name);

-- 评分排序
CREATE INDEX idx_cr_rating ON class_record(rating);
```

**答辩话术：** "目前主键索引保证了单表查询效率，搜索场景我建了 name 列索引。如果数据量增长到百万级，我还会考虑对 class_record 的 member_id 和 class_id 建联合索引，因为这两个字段经常同时出现在 JOIN 和 WHERE 条件里。"

### 1.3 有没有视图？

**已实现：`v_member_course_rating`**

这是实验四的成果。视图定义应该类似：

```sql
CREATE VIEW v_member_course_rating AS
SELECT m.member_id, m.member_name, m.gender,
       ct.class_name, cr.rating, cr.status
FROM member m
JOIN class_record cr ON m.member_id = cr.member_id
JOIN class_table ct ON cr.class_id = ct.class_id;
```

在 ExperimentController 中提供了 3 个基于视图的查询接口：
- `/api/experiment/queryViewAll` — 全部选课记录
- `/api/experiment/queryViewHighRating` — 评分 >= 8.0 的记录
- `/api/experiment/queryViewMemberAvg` — 平均分 > 8.0 的会员

**老师会问的追问：** "视图和直接查基表有什么区别？你为什么要用视图？"

→ 答案：（1）简化复杂查询，封装 JOIN 逻辑；（2）提供逻辑数据独立性；（3）可以结合权限控制，比如只给某些用户开放视图而非基表。

**但你要注意：** 你的 SQL DDL 文件里似乎还没有 `CREATE VIEW` 语句。答辩时老师打开你的 SQL 文件如果找不到视图定义，会被质疑。**务必把 CREATE VIEW 语句补充到 SQL 文件里。**

### 1.4 表之间的关联

表关系梳理：

```
member   ──1:N──> class_record ──N:1── class_table
                                                │
                                         N:1 (coach_id)
                                                │
                                         employee
                                                │
class_table ──N:M──> class_equipment ──N:M── equipment
```

**目前缺失：外键约束（FOREIGN KEY）。** 老师大概率会问。

**答辩话术：** "我在应用层通过 Service 代码维护了引用完整性。没有在数据库层加外键的原因是：（1）MyISAM 引擎不支持外键，我用的是 InnoDB 可以加但在开发阶段频繁删改数据时外键会阻碍操作；（2）实际生产环境中，我会在表稳定后补上外键约束以保证数据完整性。"

**建议：** 至少花 5 分钟给 class_record 加两条外键，让老师看到你**会写**：

```sql
ALTER TABLE class_record ADD CONSTRAINT fk_cr_member
  FOREIGN KEY (member_id) REFERENCES member(member_id);
ALTER TABLE class_record ADD CONSTRAINT fk_cr_class
  FOREIGN KEY (class_id) REFERENCES class_table(class_id);
```

### 1.5 有没有触发器、存储过程？

**当前：没有。**

这是加分项(3)的内容，如果你时间够，可以考虑加一个触发器。例如：

```sql
-- 触发器：报名课程时自动扣减会员剩余课时
DELIMITER //
CREATE TRIGGER trg_class_enroll
AFTER INSERT ON class_record
FOR EACH ROW
BEGIN
  UPDATE member SET remain_class_count = remain_class_count - 1
  WHERE member_id = NEW.member_id;
END//
DELIMITER ;
```

这个触发器 5 行代码，但答辩时说出来就是加分项。**不用多，有一个就行。**

---

## 第二块：代码检查（现场随机抽查）

### 2.1 老师会怎么查？

老师验收时可能随机挑一个功能点，让你：
1. 打开对应的后端 Controller/Service/Mapper
2. 打开对应的前端 Vue 组件
3. 解释数据从前端输入到数据库的完整流程

### 2.2 你需要能解释的"一条完整链路"

以"会员搜索"为例，准备好说出这个流程：

```
前端 MemberSelMember.vue
  → 用户输入关键词，点击查询
  → api.get('/api/member/search', { params })
  → axios 发 HTTP GET 到 localhost:8080/api/member/search
  → ApiMemberController.searchMember() 接收参数
  → MemberServiceImpl.searchMembers() 计算 offset，调用 Mapper
  → MemberMapper.searchMembers() → MemberMapper.xml 动态 SQL
  → MySQL 执行 SELECT ... FROM member WHERE ... LIMIT ...
  → 结果逐层返回到前端，v-for 渲染到 el-table
```

### 2.3 现场编程环节

老师说的"增加一个按钮，能不能当场编出来"，典型场景可能是：

> "你能不能在会员管理页面加一个'导出Excel'按钮？"

**你需要证明的不是代码量，而是知道改哪里：**

1. 前端：在 `MemberSelMember.vue` 的 `<template>` 里加 `<el-button @click="exportExcel">导出</el-button>`
2. 前端：在 `<script setup>` 里写 `function exportExcel() { ... }`
3. 后端（如需要）：在 `ApiMemberController` 加 `@GetMapping("/export")` 返回文件流
4. 数据库（如需要）：写对应的 SELECT 语句

**建议练习：** 准备 2-3 个常考场景的代码模板——导出、批量删除、按日期范围筛选。至少要能说出每一步在哪个文件、加什么代码。

### 2.4 代码真实性问题

老师可能会挑一段代码问你："这段是你写的吗？解释一下。"

**容易露馅的地方：**
- `ExperimentMapper.xml` 中查询4的双重 NOT EXISTS（如果不是自己写的，很难讲清楚逻辑）
- `ClassSelClass.vue` 中复杂的 AbortController 逻辑（这个是你自己重构过的，可以重点展示）
- `SessionAuthInterceptor` 的拦截逻辑（要能说清楚 admin 和 user 两种 session 的区分逻辑）

**建议：** 把你最自豪、写得最认真的 3 个模块标注出来（比如课程分析页、实验查询模块、角色布局），老师抽查时主动引导到这些模块。

---

## 第三块：加分项（3+X 里的 X）

根据老师原话："百万条记录测试、触发器存储过程游标、完整性命名子句、第8章任务一~四"

### 当前加分项完成度

| 加分项 | 状态 | 建议 |
|---|---|---|
| 100万条记录+性能测试 | 未做 | 如果时间允许，写个 Python/Java 脚本生成 100 万条 class_record |
| 触发器 | 未做 | 加一个报名扣课时的触发器（5行代码高性价比） |
| 存储过程 | 未做 | 可加一个"会员续课"存储过程 |
| 游标 | 未做 | 可选（一般课设不太要求） |
| 完整性命名子句 | 未做 | 在建表时给约束起名即可：`CONSTRAINT pk_member PRIMARY KEY (member_id)` |
| GRANT/REVOKE | 已做 | ExperimentMapper.xml 中有 grantDemo/revokeDemo |

### 100万条记录测试的准备工作

如果要做加分项(1)，你需要准备：

1. **数据生成脚本**（Python/Java 都可以，循环 INSERT）
2. **加索引前的查询耗时**（截图 EXPLAIN 结果）
3. **加索引后的查询耗时**（截图 EXPLAIN 结果 + 执行时间对比）
4. **优化方案说明**（在实验报告中用一张对比表展示）

即使来不及做完整的 100 万测试，**至少在报告里论述你的优化思路**：哪些列需要索引、为什么、预期效果如何。老师更看重你的分析能力，不是数据量本身。

---

## 实验报告（决定态度分）

老师原话："态度很重要，排版乱七八糟的最后分数不会高。"

按照第7章那 6 个步骤写：

| 步骤 | 内容 | 你要准备的材料 |
|---|---|---|
| 1. 需求分析 | 系统功能描述、数据流图 | 用文字+简单图表说明健身房有哪些业务需求 |
| 2. 概念结构设计 | E-R 图 | 画一个完整的 E-R 图（实体+属性+联系），标注 1:1、1:N、N:M |
| 3. 逻辑结构设计 | E-R 图转关系模式 | 写出每张表的规范化形式（至少 3NF），说明主键和外键 |
| 4. 物理结构设计 | 建表 DDL、索引、存储引擎选择 | 贴上完整 SQL，说明为什么选 InnoDB，为什么某些列用 varchar 而非 char |
| 5. 数据库实施 | 数据录入、应用开发 | 截图系统运行界面，展示 CRUD 功能 |
| 6. 运行和维护 | 备份策略、性能优化 | 如果有索引优化测试数据最好，没有则论述维护方案 |

**排版建议：**
- 表格用三线表，标题在表上方
- 图片居中，标题在下方
- 代码用等宽字体、浅灰底色
- 不要出现"如图X所示"而图在下一页的情况
- 字号统一：正文小四/12pt，标题逐级加大

---

## 答辩前检查清单

答辩前逐项确认：

- [ ] 打开 MySQL，能 `SHOW TABLES;` 看到 7 张表
- [ ] `SHOW CREATE TABLE class_record;` 能看到字段注释
- [ ] `SHOW INDEX FROM class_record;` 能看到你建的索引
- [ ] `SHOW CREATE VIEW v_member_course_rating;` 能看到视图定义
- [ ] SQL 文件中的 DDL 与实际数据库一致（**这是上次 review 发现的问题，已修复**）
- [ ] 前端能正常启动（`npm run dev`）
- [ ] 后端能正常启动（`mvn spring-boot:run`）
- [ ] 能演示完整的管理员流程：登录 → 查看会员 → 搜索 → 编辑 → 删除
- [ ] 能演示完整的会员流程：登录 → 查看课程 → 报名 → 查看我的课程
- [ ] 能打开 ExperimentController 演示实验一到五的 SQL 查询
- [ ] 准备 2-3 段"最自豪的代码"用于现场展示
- [ ] 实验报告排版检查（目录、页码、图表编号、参考文献）
- [ ] 准备好现场编程的预案（至少知道每个模块的文件位置和修改方式）

---

## 总结：你现在的位置

**已完成（基础分有保障）：**
- 7 张表设计合理，涵盖健身房核心业务流程
- 前后端完整 CRUD 功能，Vue 3 + Spring Boot 技术栈
- 实验二全部 9 组查询已实现（含 NOT EXISTS 除法、GROUP BY HAVING 等高级 SQL）
- 实验三的数据修改、实验四的视图查询、实验五的聚合统计和 GRANT/REVOKE 均已实现
- 课程分析页面（容量利用率、评分统计）是亮点，可以向老师重点展示

**答辩前需要补齐（差距项）：**
1. 在 SQL 文件中补上 `CREATE VIEW` 语句和 `CREATE INDEX` 语句
2. 至少建 3 个辅助索引（member_name、class_record 的 member_id 和 class_id）
3. 考虑加 1 个触发器（报名自动扣课时），性价比最高
4. 给 class_record 加两个外键约束（证明你知道怎么写）
5. 实验报告按 6 个步骤排版，E-R 图一定要画

**如果时间充裕可以冲加分：**
1. 写个 Python 脚本生成 100 万条 class_record 数据
2. 对比加索引前后的查询时间，截图放进报告
3. 在课程分析页加 ECharts 柱状图/Pie 图
