# 健身房管理系统 V3 重构任务单

## 当前评价

目前功能可以运行，但数据库结构升级后，前后端代码仍然保留大量旧设计。

存在：

- 命名不统一
- Entity职责混乱
- 新数据库字段未充分利用
- 器械关联功能未展示
- 可维护性下降

需要进行一次统一重构。

------

# 一级优先级（必须修改）

## 1. ClassOrder 全面重命名为 ClassRecord

当前：

```java
ClassOrder
ClassOrderMapper
ClassOrderService
ClassOrderController

ClassSelClassOrder.vue
```

数据库已经改为：

```sql
class_record
```

继续使用 ClassOrder 会导致：

业务名称与数据库结构不一致。

### 必须改为

```java
ClassRecord
ClassRecordMapper
ClassRecordService
ClassRecordController
```

前端：

```vue
ClassSelClassRecord.vue
```

相关DTO、VO同步修改。

------

## 2. status 字段显示转换

数据库：

```text
0 = 已报名
1 = 已完成
2 = 已取消
```

禁止页面直接显示：

```text
0
1
2
```

统一实现：

```javascript
const statusMap = {
    0:'已报名',
    1:'已完成',
    2:'已取消'
}
```

所有页面统一使用。

包括：

- 管理员课程记录页
- 用户我的课程页
- 课程详情页

------

## 3. classLevel 统一显示转换

数据库：

```text
1
2
3
```

禁止直接显示数字。

统一实现：

```javascript
const levelMap = {
    1:'入门',
    2:'进阶',
    3:'专业'
}
```

所有页面保持一致。

禁止出现：

```text
A页面：
初级

B页面：
进阶

C页面：
高级
```

统一命名。

------

# 二级优先级（强烈建议修改）

## 4. Entity 与 VO 分离

当前设计存在：

```java
ClassOrder
{
    recordId

    memberId

    memberName

    classId

    className

    coach

    rating

    ...
}
```

问题：

把多张表字段全部塞进一个实体类。

后期维护困难。

------

建议：

### Entity

```java
ClassRecord
```

仅保存：

```java
recordId
memberId
classId
joinTime
status
rating
totalSessions
completedSessions
```

------

### VO

```java
ClassRecordVO
```

保存联表查询结果：

```java
recordId

memberName

className

coachName

rating

status
```

------

同理：

```java
ClassTableVO
MemberVO
```

根据页面需求单独创建。

------

## 5. 教练统一使用 coachId

禁止：

```java
coachName
```

直接存字符串。

课程表统一：

```java
coachId
```

通过联表获取：

```java
employee.employeeName
```

保证数据一致性。

------

# 三级优先级（建议增加）

## 6. 增加课程详情页

当前页面：

```text
课程列表
```

只能看到：

- 名称
- 等级
- 价格

无法体现数据库新增设计。

------

建议新增：

### 课程详情弹窗

显示：

```text
课程名称

课程等级

课程价格

课程时长

课程介绍

教练信息

使用器械
```

------

## 7. 展示课程器械关联

数据库已经新增：

```sql
class_equipment
```

但前端没有任何展示。

导致：

```sql
class_equipment
```

成为摆设。

------

课程详情页展示：

```text
使用器械：

杠铃
龙门架
史密斯机
```

例如：

```text
增肌训练

使用器械：

• 杠铃
• 龙门架
• 史密斯机
```

------

## 8. 用户选课页面展示课程介绍

当前：

```text
课程名称
价格
等级
```

建议增加：

```text
课程介绍
```

让用户知道课程内容。

------

# 四级优先级（可选优化）

## 9. 评分颜色区分

例如：

```text
90~100
绿色

80~89
蓝色

60~79
橙色

60以下
红色
```

提高可读性。

------

## 10. 状态标签化

例如：

```text
已报名
已完成
已取消
```

使用 Tag 组件显示。

不要纯文本。

------

## 11. 容量显示优化

当前：

```text
maxCapacity
```

建议显示：

```text
18 / 30
```

已报名人数 / 最大人数

更符合业务场景。

------

# 不建议修改

以下设计保持不变：

```text
member

employee

class_table

class_record

equipment

class_equipment
```

数据库结构已经合理。

不建议继续大规模调整。

------

# 最终目标

完成后系统应具备：

1. 数据库结构与代码命名一致
2. 页面展示全部新字段
3. 教练与课程建立真实关联
4. 器械与课程建立真实关联
5. 课程详情信息完整展示
6. Entity、VO职责清晰
7. 满足数据库课程设计与答辩要求
8. 保持后续可维护性

```

```