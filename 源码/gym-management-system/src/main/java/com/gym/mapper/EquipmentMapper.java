package com.gym.mapper;

import com.gym.pojo.Equipment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EquipmentMapper {

    //查询所有器械
    List<Equipment> findAll();

    //根据id删除器械
    Boolean deleteByEquipmentId(Integer equipmentId);

    //添加器材
    Boolean insertEquipment(Equipment equipment);

    //根据id修改器械信息
    Boolean updateEquipmentByEquipmentId(Equipment equipment);

    //根据id查询器械
    List<Equipment> selectByEquipmentId(Integer equipmentId);

    //查询器械总数
    Integer selectTotalCount();

    //根据课程ID查询器械
    List<Equipment> selectByClassId(Integer classId);

    List<Equipment> searchEquipment(@Param("keyword") String keyword,
                                    @Param("location") String location,
                                    @Param("status") String status,
                                    @Param("offset") int offset,
                                    @Param("pageSize") int pageSize);

    int countSearchEquipment(@Param("keyword") String keyword,
                             @Param("location") String location,
                             @Param("status") String status);
}
