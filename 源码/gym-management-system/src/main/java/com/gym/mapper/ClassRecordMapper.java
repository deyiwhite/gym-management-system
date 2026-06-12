package com.gym.mapper;

import com.gym.pojo.ClassRecord;
import com.gym.pojo.ClassRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassRecordMapper {

    List<ClassRecordVO> findAll();

    Boolean insertRecord(ClassRecord record);

    List<ClassRecordVO> selectByMemberId(Integer memberId);

    Boolean deleteByRecordId(Integer recordId);

    ClassRecord selectByClassIdAndMemberId(Integer classId, Integer memberId);

    List<ClassRecordVO> selectByClassId(Integer classId);

    int countByClassId(Integer classId);

    List<ClassRecordVO> searchRecords(@Param("classId") Integer classId,
                                      @Param("memberKeyword") String memberKeyword,
                                      @Param("status") Integer status,
                                      @Param("ratingStatus") String ratingStatus,
                                      @Param("lowRating") Double lowRating,
                                      @Param("highRating") Double highRating);
}
