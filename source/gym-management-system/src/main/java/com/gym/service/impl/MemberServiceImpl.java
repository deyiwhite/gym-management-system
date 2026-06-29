package com.gym.service.impl;

import com.gym.mapper.MemberMapper;
import com.gym.pojo.Member;
import com.gym.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public List<Member> findAll() {
        return memberMapper.findAll();
    }

    @Override
    public Boolean insertMember(Member member) {
        return memberMapper.insertMember(member);
    }

    @Override
    public Boolean updateMemberByMemberAccount(Member member) {
        return memberMapper.updateMemberByMemberAccount(member);
    }

    @Override
    public Member userLogin(Member member) {
        return memberMapper.selectByAccountAndPassword(member);
    }

    @Override
    public Boolean deleteByMemberAccount(String memberAccount) {
        return memberMapper.deleteByMemberAccount(memberAccount);
    }

    @Override
    public Integer selectTotalCount() {
        return memberMapper.selectTotalCount();
    }

    @Override
    public List<Member> selectByMemberAccount(String memberAccount) {
        return memberMapper.selectByMemberAccount(memberAccount);
    }

    @Override
    public List<Member> searchMembers(String keyword, String gender, Integer minRemain, Integer maxRemain, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;
        int offset = (page - 1) * pageSize;
        return memberMapper.searchMembers(keyword, gender, minRemain, maxRemain, offset, pageSize);
    }

    @Override
    public int countSearchMembers(String keyword, String gender, Integer minRemain, Integer maxRemain) {
        return memberMapper.countSearchMembers(keyword, gender, minRemain, maxRemain);
    }

    @Override
    public String selectMaxNoByYear(String yearPrefix) {
        return memberMapper.selectMaxNoByYear(yearPrefix);
    }

    @Override
    public int updateRemainClassCount(String memberAccount, int delta) {
        return memberMapper.updateRemainClassCount(memberAccount, delta);
    }
}
