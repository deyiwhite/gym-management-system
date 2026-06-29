package com.gym.controller;

import com.gym.service.ExperimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/experiment")
public class ExperimentController {

    @Autowired
    private ExperimentService experimentService;

    @GetMapping("/query1")
    public Map<String, Object> query1() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", experimentService.query1_yogaMembers());
        resp.put("title", "查询(1): 报名瑜伽课的会员");
        return resp;
    }

    @GetMapping("/query2")
    public Map<String, Object> query2() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", experimentService.query2_zengjiMembers());
        resp.put("title", "查询(2): 报名增肌课的会员");
        return resp;
    }

    @GetMapping("/query3")
    public Map<String, Object> query3() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", experimentService.query3_notYogaMembers());
        resp.put("title", "查询(3): 没报名瑜伽课的会员");
        return resp;
    }

    @GetMapping("/query4")
    public Map<String, Object> query4() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", experimentService.query4_allCoursesMember());
        resp.put("title", "查询(4): 报名全部课程的会员");
        return resp;
    }

    @GetMapping("/query5")
    public Map<String, Object> query5() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", experimentService.query5_passExceptYoga());
        resp.put("title", "查询(5): 除瑜伽外全及格的会员与平均评分");
        return resp;
    }

    @GetMapping("/query6")
    public Map<String, Object> query6() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", experimentService.query6_zengjiSecond());
        resp.put("title", "查询(6): 增肌课评分第2名");
        return resp;
    }

    @GetMapping("/query7")
    public Map<String, Object> query7() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", experimentService.query7_advancedCourses());
        resp.put("title", "查询(7): 专业课程>=3门8.0+的会员");
        return resp;
    }

    @GetMapping("/query8")
    public Map<String, Object> query8() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", experimentService.query8_uniqueCount());
        resp.put("title", "查询(8): 报名门数唯一的会员");
        return resp;
    }

    @GetMapping("/query9a")
    public Map<String, Object> query9a(@RequestParam(defaultValue = "李") String keyword) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", experimentService.query9a_like(keyword));
        resp.put("title", "查询(9a): LIKE '%" + keyword + "%'");
        return resp;
    }

    @GetMapping("/query9b")
    public Map<String, Object> query9b(@RequestParam Double low, @RequestParam Double high) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", experimentService.query9b_between(low, high));
        resp.put("title", "查询(9b): BETWEEN " + low + " AND " + high);
        return resp;
    }

    @GetMapping("/query9c")
    public Map<String, Object> query9c(@RequestParam List<String> names) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", experimentService.query9c_in(names));
        resp.put("title", "查询(9c): IN " + names);
        return resp;
    }

    @GetMapping("/query9d")
    public Map<String, Object> query9d() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", experimentService.query9d_isNull());
        resp.put("title", "查询(9d): IS NULL 已报名未评分的记录");
        return resp;
    }

    @GetMapping("/query9e")
    public Map<String, Object> query9e() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", experimentService.query9e_distinct());
        resp.put("title", "查询(9e): DISTINCT 有会员报名的课程名");
        return resp;
    }

    @GetMapping("/query9f")
    public Map<String, Object> query9f(@RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", experimentService.query9f_orderBy(limit));
        resp.put("title", "查询(9f): ORDER BY 评分降序 Top " + limit);
        return resp;
    }

    @GetMapping("/query9g")
    public Map<String, Object> query9g(@RequestParam(defaultValue = "7.0") Double minAvg) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", experimentService.query9g_groupByHaving(minAvg));
        resp.put("title", "查询(9g): GROUP BY+HAVING 平均评分>=" + minAvg + "的课程");
        return resp;
    }

    // ========== 实验三: 数据修改 ==========

    @PostMapping("/finishCourse")
    public Map<String, Object> finishCourse(@RequestParam int classId) {
        int rows = experimentService.finishCourse(classId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("affectedRows", rows);
        resp.put("title", "课程结课: 课程" + classId + " 共" + rows + "条报名记录已标记为完成");
        return resp;
    }

    @PostMapping("/cancelCourse")
    public Map<String, Object> cancelCourse(@RequestParam int classId) {
        int rows = experimentService.cancelCourse(classId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("affectedRows", rows);
        resp.put("title", "课程取消: 课程" + classId + " 共" + rows + "条报名记录已删除");
        return resp;
    }

    @PostMapping("/cancelMember")
    public Map<String, Object> cancelMember(@RequestParam int memberId) {
        int rows = experimentService.cancelMember(memberId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("affectedRows", rows);
        resp.put("title", "会员注销: 会员" + memberId + " 共" + rows + "条报名记录已删除");
        return resp;
    }

    // ========== 实验四: 视图操作 ==========

    @GetMapping("/queryViewAll")
    public Map<String, Object> queryViewAll() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", experimentService.queryViewAll());
        resp.put("title", "视图查询: 全部会员选课评分");
        return resp;
    }

    @GetMapping("/queryViewHighRating")
    public Map<String, Object> queryViewHighRating() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", experimentService.queryViewHighRating());
        resp.put("title", "视图查询: 评分>=8.0的选课记录");
        return resp;
    }

    @GetMapping("/queryViewMemberAvg")
    public Map<String, Object> queryViewMemberAvg() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", experimentService.queryViewMemberAvg());
        resp.put("title", "视图查询: 平均分>8.0的会员");
        return resp;
    }

    // ========== 实验五: 库函数 + 授权 ==========

    @GetMapping("/queryMemberStats")
    public Map<String, Object> queryMemberStats() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("data", experimentService.queryMemberStats());
        resp.put("title", "统计: 每会员报名门数与平均评分");
        return resp;
    }

    @PostMapping("/grantDemo")
    public Map<String, Object> grantDemo() {
        Map<String, Object> resp = new HashMap<>();
        try {
            experimentService.grantDemo();
            resp.put("success", true);
            resp.put("message", "GRANT 执行成功");
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", "GRANT 失败(可能权限不足): " + e.getMessage());
        }
        return resp;
    }

    @PostMapping("/revokeDemo")
    public Map<String, Object> revokeDemo() {
        Map<String, Object> resp = new HashMap<>();
        try {
            experimentService.revokeDemo();
            resp.put("success", true);
            resp.put("message", "REVOKE 执行成功");
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", "REVOKE 失败(可能权限不足): " + e.getMessage());
        }
        return resp;
    }
}
