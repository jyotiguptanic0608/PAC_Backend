package com.proposal.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;

import com.proposal.entity.ProposalReview;
import java.time.LocalDateTime;
import com.proposal.entity.Proposal;
import com.proposal.repository.ProposalRepository;
import com.proposal.service.ProposalService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import com.proposal.entity.Employee;
import com.proposal.repository.ProposalReviewRepository;
import com.proposal.repository.EmployeeRepository;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

@Service
public class ProposalServiceImpl implements ProposalService{
	private final ProposalRepository proposalRepo;

private final ProposalReviewRepository reviewRepo;

private final EmployeeRepository employeeRepo;
	
	
	public ProposalServiceImpl(
        ProposalRepository proposalRepo,
        ProposalReviewRepository reviewRepo,
        EmployeeRepository employeeRepo) {

    this.proposalRepo = proposalRepo;
    this.reviewRepo = reviewRepo;
    this.employeeRepo = employeeRepo;
}

@Override
public Proposal saveProposal(Proposal proposal, MultipartFile[] files) {

    List<String> fileNames = new ArrayList<>();

    try {
        Path uploadPath = Paths.get("uploads");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        for (MultipartFile file : files) {

            String fileName = file.getOriginalFilename();
            fileNames.add(fileName);

            Path filePath = uploadPath.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

    } catch (IOException e) {
        throw new RuntimeException(e);
    }

    proposal.setFile(String.join(",", fileNames));

    return proposalRepo.save(proposal);
}
	
	public List<Proposal> getAll(){
		return proposalRepo.findAll();
	}
	
	 @Override
	 public Proposal getProposalById(Long id) {

	     return proposalRepo.findById(id)
	                .orElseThrow();
	 }

	    @Override
	    public Proposal approveProposal(Long id) {

	        Proposal proposal =
	                proposalRepo.findById(id).orElse(null);

	        proposal.setStatus("APPROVED");

	        return proposalRepo.save(proposal);
	    }
@Override
public void reviewProposal(
        Long proposalId,
        Long reviewerId,
        String reviewText) {

    Proposal proposal =
            proposalRepo.findById(proposalId)
            .orElseThrow();

    Employee reviewer =
            employeeRepo.findById(reviewerId)
            .orElseThrow();

    ProposalReview review =
            new ProposalReview();

    review.setProposal(proposal);

    review.setReviewer(reviewer);

    review.setRemarks(reviewText);

    review.setReviewDate(LocalDateTime.now());
review.setProposalFile(proposal.getFile());

    reviewRepo.save(review);

    proposal.setStatus("CHANGES_REQUIRED");

    proposalRepo.save(proposal);

}
            @Override
public List<Proposal> getEmployeeProposals(Long employeeId) {
    return proposalRepo.findByEmployee_Id(employeeId);
}

@Override
public List<ProposalReview> getProposalReviews(
        Long proposalId) {

    return reviewRepo.findByProposalId(proposalId);

}
}