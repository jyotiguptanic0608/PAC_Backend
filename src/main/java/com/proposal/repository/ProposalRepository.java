package com.proposal.repository;
import java.util.List;

import com.proposal.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProposalRepository extends JpaRepository<Proposal,Long>{
   List<Proposal> findByEmployee_Id(Long employeeId);
   List<Proposal> findByEmployee_IdOrderByIdDesc(Long employeeId);
   List<Proposal> findAllByOrderByIdDesc();
}