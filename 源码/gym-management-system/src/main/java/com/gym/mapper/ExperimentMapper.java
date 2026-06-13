package com.gym.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ExperimentMapper {

    // 查询(1): 报名瑜伽课的会员
    List<Map<String, Object>> query1_yogaMembers();

    // 查询(2): 报名增肌课的会员
    List<Map<String, Object>> query2_zengjiMembers();

    // 查询(3): 没报名瑜伽课的会员
    List<Map<String, Object>> query3_notYogaMembers();

    // 查询(4): 报名全部课程的会员 (除法)
    List<Map<String, Object>> query4_allCoursesMember();

    // 查询(5): 除瑜伽外全及格的会员
    List<Map<String, Object>> query5_passExceptYoga();

    // 查询(6): 增肌课评分第2名
    List<Map<String, Object>> query6_zengjiSecond();

    // 查询(7): 高级课>=3门8.0+的会员
    List<Map<String, Object>> query7_advancedCourses();

    // 查询(8): 报名门数唯一的会员
    List<Map<String, Object>> query8_uniqueCount();

    // 查询(9a): LIKE
    List<Map<String, Object>> query9a_like(String keyword);

    // 查询(9b): BETWEEN
    List<Map<String, Object>> query9b_between(Double low, Double high);

    // 查询(9c): IN
    List<Map<String, Object>> query9c_in(List<String> names);

    // 查询(9d): IS NULL
    List<Map<String, Object>> query9d_isNull();

    // 查询(9e): DISTINCT
    List<Map<String, Object>> query9e_distinct();

    // 查询(9f): ORDER BY
    List<Map<String, Object>> query9f_orderBy(int limit);

    // 查询(9g): GROUP BY + HAVING
    List<Map<String, Object>> query9g_groupByHaving(Double minAvg);

    // 实验三: 数据修改
    int finishCourse(int classId);
    int cancelCourse(int classId);
    int cancelMember(int memberId);

    // 实验四: 视图查询
    List<Map<String, Object>> queryViewAll();
    List<Map<String, Object>> queryViewHighRating();
    List<Map<String, Object>> queryViewMemberAvg();

    // 实验五: 库函数 + 授权
    List<Map<String, Object>> queryMemberStats();
    void grantDemo();
    void revokeDemo();
}
