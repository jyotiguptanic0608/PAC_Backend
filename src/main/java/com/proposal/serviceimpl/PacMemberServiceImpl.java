package com.proposal.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.proposal.entity.PacMember;
import com.proposal.repository.PacMemberRepository;
import com.proposal.service.PacMemberService;


@Service
public class PacMemberServiceImpl implements PacMemberService {

    private final PacMemberRepository pacMemberRepo;

    public PacMemberServiceImpl(PacMemberRepository pacMemberRepo) {
        this.pacMemberRepo = pacMemberRepo;
    }

    @Override
    public PacMember getPacMember(Long employeeId) {

        return pacMemberRepo
                .findByEmployeeId(employeeId)
                .orElseThrow(() ->
                        new RuntimeException("PAC Member not found"));

    }

    @Override
    public List<PacMember> getAllPacMembers() {

        return pacMemberRepo.findAll();

    }

}