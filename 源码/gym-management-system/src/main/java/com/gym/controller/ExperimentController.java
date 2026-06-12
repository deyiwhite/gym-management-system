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
}
