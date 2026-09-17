package com.proposal.repository;

import com.proposal.entity.ProposalRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProposalRevisionRepository extends JpaRepository<ProposalRevision, Long> {
    List<ProposalRevision> findByProposalIdOrderByVersionNumberAsc(Long proposalId);
}
