package com.gym.service.impl;

import com.gym.mapper.ExperimentMapper;
import com.gym.service.ExperimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ExperimentServiceImpl implements ExperimentService {

    @Autowired
    private ExperimentMapper experimentMapper;

    @Override public List<Map<String, Object>> query1_yogaMembers() { return experimentMapper.query1_yogaMembers(); }
    @Override public List<Map<String, Object>> query2_zengjiMembers() { return experimentMapper.query2_zengjiMembers(); }
    @Override public List<Map<String, Object>> query3_notYogaMembers() { return experimentMapper.query3_notYogaMembers(); }
    @Override public List<Map<String, Object>> query4_allCoursesMember() { return experimentMapper.query4_allCoursesMember(); }
    @Override public List<Map<String, Object>> query5_passExceptYoga() { return experimentMapper.query5_passExceptYoga(); }
    @Override public List<Map<String, Object>> query6_zengjiSecond() { return experimentMapper.query6_zengjiSecond(); }
    @Override public List<Map<String, Object>> query7_advancedCourses() { return experimentMapper.query7_advancedCourses(); }
    @Override public List<Map<String, Object>> query8_uniqueCount() { return experimentMapper.query8_uniqueCount(); }
    @Override public List<Map<String, Object>> query9a_like(String keyword) { return experimentMapper.query9a_like(keyword); }
    @Override public List<Map<String, Object>> query9b_between(Double low, Double high) { return experimentMapper.query9b_between(low, high); }
    @Override public List<Map<String, Object>> query9c_in(List<String> names) { return experimentMapper.query9c_in(names); }
    @Override public List<Map<String, Object>> query9d_isNull() { return experimentMapper.query9d_isNull(); }
    @Override public List<Map<String, Object>> query9e_distinct() { return experimentMapper.query9e_distinct(); }
    @Override public List<Map<String, Object>> query9f_orderBy(int limit) { return experimentMapper.query9f_orderBy(limit); }
    @Override public List<Map<String, Object>> query9g_groupByHaving(Double minAvg) { return experimentMapper.query9g_groupByHaving(minAvg); }
}
