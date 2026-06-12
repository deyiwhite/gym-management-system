# 健身房管理系统 UI 重构计划（基于 review1.md）

## 一级优先级（必须改）

### 1. ClassOrder → ClassRecord 全局重命名
**后端：**
- 重命名 POJO: `ClassOrder.java` → `ClassRecord.java`，去掉冗余字段（className/coach/memberName/classBegin），只保留 DB 对应字段
- 重命名 Mapper: `ClassOrderMapper.java/.xml` → `ClassRecordMapper.java/.xml`
- 重命名 Service: `ClassOrderService.java` → `ClassRecordService.java`, `ClassOrderServiceImpl.java` → `ClassRecordServiceImpl.java`
- Controller 里引用也跟着改

**前端：**
- 重命名页面: `ClassSelClassOrder.vue` → `ClassSelClassRecord.vue`
- 路由、侧边栏菜单同步改

### 2. status 字段显示转换
- 所有页面统一用 `{0:'已报名', 1:'已完成', 2:'已取消'}` 映射
- 涉及页面：管理员课程记录页、用户我的课程页、课程详情

### 3. classLevel 统一显示转换
- 所有页面统一用 `{1:'入门', 2:'进阶', 3:'专业'}`
- 禁止出现"初级/中级/高级"

---

## 二级优先级（强烈建议）

### 4. Entity/VO 分离
- `ClassRecord`（Entity）：只存 DB 字段（recordId, memberId, classId, joinTime, status, rating, totalSessions, completedSessions）
- `ClassRecordVO`（VO）：存联表查询结果（memberName, className, coachName, rating, status）
- 同理创建 `ClassTableVO`、`MemberVO`

### 5. 教练统一用 coachId（FK）
- ClassTable POJO 里 coach 改成 coachId
- 通过 JOIN employee 拿 coachName
- 添加课程时 coach 从下拉选员工

---

## 三级优先级（建议增加）

### 6. 增加课程详情弹窗/页面
- 显示：课程名称、等级、价格、时长、介绍、教练信息、使用器械列表

### 7. 展示课程-器械关联
- 课程详情中显示该课程使用的器械列表（从 class_equipment 联表查）
- 增肌 → 杠铃、哑铃等

### 8. 用户选课页展示课程介绍
- 让用户看到 class_description，不只是名称和价格

---

## 四级优先级（可选优化）

### 9. 评分颜色区分
- 8.0+ 绿色、6.0-7.9 蓝色、5.0-5.9 橙色、<5.0 红色

### 10. 状态用 Tag 组件
- 已报名=info、已完成=success、已取消=danger

### 11. 容量显示优化
- 不显示纯数字 "30"，显示 "已报人数 / 30"

---

## 实施顺序

先做一级（命名统一 + 字段映射），确保系统可维护。再做二级三级（VO分离 + 详情页 + 器械关联），最后做四级（视觉优化）。

## 验证

每做完一级就让你看一下，确保方向对再继续。
