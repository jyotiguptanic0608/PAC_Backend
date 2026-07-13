package com.proposal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proposal.entity.PacMember;

public interface PacMemberRepository
        extends JpaRepository<PacMember, Long> {

    Optional<PacMember> findByEmployeeId(Long employeeId);

}