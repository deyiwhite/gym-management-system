package com.gym.service;

import java.util.List;
import java.util.Map;

public interface ExperimentService {
    List<Map<String, Object>> query1_yogaMembers();
    List<Map<String, Object>> query2_zengjiMembers();
    List<Map<String, Object>> query3_notYogaMembers();
    List<Map<String, Object>> query4_allCoursesMember();
    List<Map<String, Object>> query5_passExceptYoga();
    List<Map<String, Object>> query6_zengjiSecond();
    List<Map<String, Object>> query7_advancedCourses();
    List<Map<String, Object>> query8_uniqueCount();
    List<Map<String, Object>> query9a_like(String keyword);
    List<Map<String, Object>> query9b_between(Double low, Double high);
    List<Map<String, Object>> query9c_in(List<String> names);
    List<Map<String, Object>> query9d_isNull();
    List<Map<String, Object>> query9e_distinct();
    List<Map<String, Object>> query9f_orderBy(int limit);
    List<Map<String, Object>> query9g_groupByHaving(Double minAvg);
}
