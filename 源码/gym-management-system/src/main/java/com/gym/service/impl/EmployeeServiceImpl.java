package com.gym.service.impl;

import com.gym.mapper.EmployeeMapper;
import com.gym.pojo.Employee;
import com.gym.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public List<Employee> findAll() {
        return employeeMapper.findAll();
    }

    @Override
    public Boolean deleteByEmployeeAccount(String employeeAccount) {
        return employeeMapper.deleteByEmployeeAccount(employeeAccount);
    }

    @Override
    public Boolean insertEmployee(Employee employee) {
        return employeeMapper.insertEmployee(employee);
    }

    @Override
    public Boolean updateMemberByEmployeeAccount(Employee employee) {
        return employeeMapper.updateMemberByEmployeeAccount(employee);
    }

    @Override
    public List<Employee> selectByEmployeeAccount(String employeeAccount) {
        return employeeMapper.selectByEmployeeAccount(employeeAccount);
    }

    @Override
    public Integer selectTotalCount() {
        return employeeMapper.selectTotalCount();
    }

    @Override
    public List<Employee> searchEmployees(String keyword, String gender, String staff, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;
        int offset = (page - 1) * pageSize;
        return employeeMapper.searchEmployees(keyword, gender, staff, offset, pageSize);
    }

    @Override
    public int countSearchEmployees(String keyword, String gender, String staff) {
        return employeeMapper.countSearchEmployees(keyword, gender, staff);
    }

    @Override
    public String selectMaxEmployeeNo() {
        return employeeMapper.selectMaxEmployeeNo();
    }
}
