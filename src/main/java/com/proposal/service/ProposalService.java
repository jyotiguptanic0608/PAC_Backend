package com.proposal.service;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.proposal.entity.ProposalReview;
import com.proposal.entity.Proposal;
public interface ProposalService {
    Proposal saveProposal(
            Proposal proposal,
            MultipartFile[] files
    );
    List<Proposal> getAll();
    void reviewProposal(
            Long proposalId,
            Long reviewerId,
            String review
    );
    Proposal approveProposal(Long id);
    Proposal getProposalById(Long id);
    List<Proposal> getEmployeeProposals(Long employeeId);
    List<ProposalReview> getProposalReviews(Long proposalId);
}