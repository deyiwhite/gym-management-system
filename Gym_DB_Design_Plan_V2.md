# 健身房管理系统数据库设计 Plan V2

## 一、总体目标

满足数据库实验1~6要求，同时保持真实健身房业务逻辑。

---

# 二、ER图

```text
                    ┌──────────────┐
                    │   employee   │
                    │    员工表     │
                    └──────┬───────┘
                           │1
                           │
                           │N
                    ┌──────▼───────┐
                    │ class_table  │
                    │    课程表     │
                    └──────┬───────┘
                           │
               ┌───────────┼───────────┐
               │           │           │
               │N          │N          │N
               │           │           │
      ┌────────▼───┐       │      ┌────▼─────────┐
      │class_record│       │      │class_equipment│
      │ 课程记录表   │       │      │ 课程器械关系表 │
      └──────┬─────┘       │      └────┬─────────┘
             │             │           │
             │N            │           │N
             │             │           │
             │1            │           │1
      ┌──────▼─────┐       │      ┌────▼──────┐
      │   member   │       │      │ equipment │
      │   会员表    │       │      │  器械表   │
      └────────────┘       │      └───────────┘
```

---

# 三、核心表设计

## 1. member（会员表）

```sql
member(
    member_id,
    member_name,
    gender,
    age,
    phone,
    register_date,
    total_class_count,
    remain_class_count
)
```

## 2. employee（员工表）

```sql
employee(
    employee_id,
    employee_name,
    gender,
    age,
    position,
    entry_date
)
```

## 3. class_table（课程表）

```sql
class_table(
    class_id,
    class_name,
    coach_id,
    class_begin,
    class_duration,
    class_level,
    max_capacity,
    class_description,
    price
)
```

课程等级：

- 1 = 初级
- 2 = 中级
- 3 = 高级

## 4. class_record（课程记录表）

```sql
class_record(
    record_id,
    member_id,
    class_id,
    join_time,
    status,
    total_sessions,
    completed_sessions,
    rating
)
```

状态：

- 0 = 已报名
- 1 = 已完成
- 2 = 已取消

rating：

- 0~100
- 用于实验中的成绩、平均分、排名等功能

## 5. equipment（器械表）

```sql
equipment(
    equipment_id,
    equipment_name,
    location,
    status,
    remark
)
```

## 6. class_equipment（课程器械关系表）

```sql
class_equipment(
    id,
    class_id,
    equipment_id
)
```

---

# 四、实验映射

| 教学数据库 | 健身房系统 |
|-----------|-----------|
| S | member |
| C | class_table |
| SC | class_record |
| Grade | rating |
| Ccredit | class_level |

---

# 五、最终关系结构

```text
member
   │
   ▼
class_record
   ▲
   │
class_table
   │
   ├────► employee
   │
   ▼
class_equipment
   │
   ▼
equipment
```

---

# 六、后续任务

1. 审查现有 SQL
2. 调整字段结构
3. 完成实验二全部查询
4. 设计测试数据
5. 实现前后端功能
6. 写报告
