# Review 报告 1-6 项修复计划

## Context

基于 `review-report.md` 的建议，对健身房管理系统进行 1-6 项改进。实验三~五融入健身房真实业务场景。

---

## 第 1 项：补齐实验三~五

### 实验三：UPDATE + DELETE

四个操作，全部管理员单击触发，SQL 层面批量：

**课程结课**（ClassSelClass.vue 课程列表每行加"结课"按钮）
- `POST /api/experiment/finishCourse` → `UPDATE class_record SET status=1 WHERE class_id=? AND status=0`
- 返回影响行数

**课程取消**（ClassSelClass.vue 课程列表每行加"取消课程"按钮）
- `POST /api/experiment/cancelCourse` → `DELETE FROM class_record WHERE class_id=?`
- 返回删除行数，弹窗确认后执行

**会员注销**（MemberSelMember.vue 会员列表每行加"注销"按钮）
- `POST /api/experiment/cancelMember` → `DELETE FROM class_record WHERE member_id=?`
- 返回删除行数，弹窗确认后执行

**器材状态修复**（EquipmentToUpdateEquipment.vue）
- 状态字段从自由输入改为下拉选择：正常 / 维修中 / 已报废

前端：ClassSelClassRecord.vue 加"实验三 · 数据修改"卡片展示 3 个按钮 + 参数输入 + 结果反馈。

### 实验四：视图操作

- SQL 创建 `v_member_course_rating` 视图
- 3 个查询端点：查全部 / 高分筛选 / 每人平均分
- 前端加"实验四 · 视图查询"卡片

### 实验五：库函数 + 授权

- 每会员报名门数+平均评分统计端点
- GRANT / REVOKE 演示端点（捕获权限异常）

---

## 第 2 项：更新 DDL + 补索引

- 重写 `gym_management_system.sql` 对齐实际 schema
- 追加 `member(member_name)`、`employee(position)` 索引

---

## 第 3 项：加 `@Transactional`

- `ClassTableServiceImpl.deleteClassByClassId()` 加事务注解

---

## 第 4 项：替换 `alert()` / `confirm()`

- `confirm()` → `ElMessageBox.confirm`
- `alert()` → `ElMessage.error/warning`

---

## 第 5 项：登录组件去重

- 抽取 `LoginPage.vue`，AdminLogin / UserLogin 改为薄壳

---

## 第 6 项：修复已知 Bug

- `ClassSelClassRecord.vue` URL 修复：`/q5`→`/query5`、`/q6`→`/query6`
- `router/index.ts` verifiedRole 登出后刷新

---

## 执行顺序（逐步验收）

1. 实验三
2. 实验四
3. 实验五
4. 更新 DDL + 补索引
5. `@Transactional`
6. 替换 alert/confirm
7. 登录组件去重
8. 修复已知 Bug
