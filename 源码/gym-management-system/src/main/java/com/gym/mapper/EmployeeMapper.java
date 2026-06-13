package com.gym.mapper;

import com.gym.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    //查询所有员工
    List<Employee> findAll();

    //根据员工账号删除员工
    Boolean deleteByEmployeeAccount(Integer employeeAccount);

    //添加新员工
    Boolean insertEmployee(Employee employee);

    //根据员工账号修改会员信息
    Boolean updateMemberByEmployeeAccount(Employee employee);

    //根据员工账号查询员工
    List<Employee> selectByEmployeeAccount(Integer employeeAccount);

    //查询员工数
    Integer selectTotalCount();

    List<Employee> searchEmployees(@Param("keyword") String keyword,
                                   @Param("gender") String gender,
                                   @Param("staff") String staff,
                                   @Param("offset") int offset,
                                   @Param("pageSize") int pageSize);

    int countSearchEmployees(@Param("keyword") String keyword,
                             @Param("gender") String gender,
                             @Param("staff") String staff);

}
