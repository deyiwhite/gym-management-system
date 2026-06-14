package com.gym.service;

import com.gym.pojo.Member;

import java.util.List;

public interface MemberService {

    //查询会员信息
    List<Member> findAll();

    //新增会员信息
    Boolean insertMember(Member member);

    //根据会员账号修改会员信息
    Boolean updateMemberByMemberAccount(Member member);

    //查询会员账号密码（登录）
    Member userLogin(Member member);
    //Member selectByAccountAndPassword(Member member);

    //根据会员账号删除会员信息
    Boolean deleteByMemberAccount(String memberAccount);

    //查询会员数
    Integer selectTotalCount();

    //根据会员账号查询会员
    List<Member> selectByMemberAccount(String memberAccount);

    List<Member> searchMembers(String keyword, String gender, Integer minRemain, Integer maxRemain, int page, int pageSize);

    int countSearchMembers(String keyword, String gender, Integer minRemain, Integer maxRemain);

    String selectMaxNoByYear(String yearPrefix);

    int updateRemainClassCount(String memberAccount, int delta);

}
