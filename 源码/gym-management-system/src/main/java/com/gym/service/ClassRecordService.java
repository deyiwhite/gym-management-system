package com.gym.service;

import com.gym.pojo.ClassRecord;
import com.gym.pojo.ClassRecordVO;

import java.util.List;

public interface ClassRecordService {
    List<ClassRecordVO> findAll();
    Boolean insertRecord(ClassRecord record);
    List<ClassRecordVO> selectByMemberId(Integer memberId);
    Boolean deleteByRecordId(Integer recordId);
    ClassRecord selectByClassIdAndMemberId(Integer classId, Integer memberId);
    List<ClassRecordVO> selectByClassId(Integer classId);
    int countByClassId(Integer classId);
    List<ClassRecordVO> searchRecords(Integer classId, String memberKeyword, Integer status,
                                      String ratingStatus, Double lowRating, Double highRating);
}
