package com.proposal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proposal.entity.ProposalReview;

public interface ProposalReviewRepository
        extends JpaRepository<ProposalReview, Long> {

    List<ProposalReview> findByProposalId(Long proposalId);

}