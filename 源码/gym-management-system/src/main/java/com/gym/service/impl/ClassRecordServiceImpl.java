package com.gym.service.impl;

import com.gym.mapper.ClassRecordMapper;
import com.gym.pojo.ClassRecord;
import com.gym.pojo.ClassRecordVO;
import com.gym.service.ClassRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassRecordServiceImpl implements ClassRecordService {

    @Autowired
    private ClassRecordMapper classRecordMapper;

    @Override public List<ClassRecordVO> findAll() { return classRecordMapper.findAll(); }
    @Override public Boolean insertRecord(ClassRecord record) { return classRecordMapper.insertRecord(record); }
    @Override public List<ClassRecordVO> selectByMemberId(Integer memberId) { return classRecordMapper.selectByMemberId(memberId); }
    @Override public Boolean deleteByRecordId(Integer recordId) { return classRecordMapper.deleteByRecordId(recordId); }
    @Override public ClassRecord selectByClassIdAndMemberId(Integer classId, Integer memberId) { return classRecordMapper.selectByClassIdAndMemberId(classId, memberId); }
    @Override public List<ClassRecordVO> selectByClassId(Integer classId) { return classRecordMapper.selectByClassId(classId); }
    @Override public int countByClassId(Integer classId) { return classRecordMapper.countByClassId(classId); }
    @Override public List<ClassRecordVO> searchRecords(Integer classId, String memberKeyword, Integer status,
                                                       String ratingStatus, Double lowRating, Double highRating) {
        return classRecordMapper.searchRecords(classId, memberKeyword, status, ratingStatus, lowRating, highRating);
    }
}
