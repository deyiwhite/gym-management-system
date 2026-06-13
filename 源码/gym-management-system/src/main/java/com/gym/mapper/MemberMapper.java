package com.gym.mapper;

import com.gym.pojo.Member;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MemberMapper {

    //查询会员信息
    List<Member> findAll();

    //新增会员信息
    Boolean insertMember(Member member);

    //根据会员账号修改会员信息
    Boolean updateMemberByMemberAccount(Member member);

    //查询会员账号密码
    Member selectByAccountAndPassword(Member member);

    //根据会员账号删除会员信息
    Boolean deleteByMemberAccount(Integer memberAccount);

    //查询会员数
    Integer selectTotalCount();

    //根据会员账号查询会员
    List<Member> selectByMemberAccount(Integer memberAccount);

    List<Member> searchMembers(@Param("keyword") String keyword,
                               @Param("gender") String gender,
                               @Param("minRemain") Integer minRemain,
                               @Param("maxRemain") Integer maxRemain,
                               @Param("offset") int offset,
                               @Param("pageSize") int pageSize);

    int countSearchMembers(@Param("keyword") String keyword,
                           @Param("gender") String gender,
                           @Param("minRemain") Integer minRemain,
                           @Param("maxRemain") Integer maxRemain);

}
