package com.gym.service.impl;

import com.gym.mapper.EquipmentMapper;
import com.gym.pojo.Equipment;
import com.gym.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Override
    public List<Equipment> findAll() {
        return equipmentMapper.findAll();
    }

    @Override
    public Boolean deleteByEquipmentId(Integer equipmentId) {
        return equipmentMapper.deleteByEquipmentId(equipmentId);
    }

    @Override
    public Boolean insertEquipment(Equipment equipment) {
        return equipmentMapper.insertEquipment(equipment);
    }

    @Override
    public Boolean updateEquipmentByEquipmentId(Equipment equipment) {
        return equipmentMapper.updateEquipmentByEquipmentId(equipment);
    }

    @Override
    public List<Equipment> selectByEquipmentId(Integer equipmentId) {
        return equipmentMapper.selectByEquipmentId(equipmentId);
    }

    @Override
    public Integer selectTotalCount() {
        return equipmentMapper.selectTotalCount();
    }

    @Override
    public List<Equipment> selectByClassId(Integer classId) {
        return equipmentMapper.selectByClassId(classId);
    }

    @Override
    public List<Equipment> searchEquipment(String keyword, String location, String status, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;
        int offset = (page - 1) * pageSize;
        return equipmentMapper.searchEquipment(keyword, location, status, offset, pageSize);
    }

    @Override
    public int countSearchEquipment(String keyword, String location, String status) {
        return equipmentMapper.countSearchEquipment(keyword, location, status);
    }
}
