package com.proposal.service;

import java.util.List;

import com.proposal.entity.PacMember;

public interface PacMemberService {

    PacMember getPacMember(Long employeeId);

    List<PacMember> getAllPacMembers();

}