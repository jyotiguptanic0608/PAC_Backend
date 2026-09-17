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
import org.springframework.web.multipart.MultipartFile;
import com.proposal.entity.ProposalRevision;
import com.proposal.repository.ProposalRevisionRepository;

import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ProposalServiceImpl implements ProposalService {
    private final ProposalRepository proposalRepo;
    private final ProposalReviewRepository reviewRepo;
    private final EmployeeRepository employeeRepo;
    private final ProposalRevisionRepository revisionRepo;

    @Value("${file.upload-dir:D:/PAC_Proposals}")
    private String uploadDir;

    public ProposalServiceImpl(
            ProposalRepository proposalRepo,
            ProposalReviewRepository reviewRepo,
            EmployeeRepository employeeRepo,
            ProposalRevisionRepository revisionRepo) {

        this.proposalRepo = proposalRepo;
        this.reviewRepo = reviewRepo;
        this.employeeRepo = employeeRepo;
        this.revisionRepo = revisionRepo;
    }

    private String generateCustomFileName(Proposal proposal, String originalFilename, int versionNumber, int fileIndex, int totalFiles) {
        String deptName = proposal.getDepartmentName();
        String deptSanitized = (deptName != null && !deptName.trim().isEmpty())
                ? deptName.trim().replaceAll("[^a-zA-Z0-9]", "_")
                : "DEPT";

        Employee employee = proposal.getEmployee();
        String empIdSanitized = "EMP";
        if (employee != null) {
            if (employee.getUsername() != null && !employee.getUsername().trim().isEmpty()) {
                empIdSanitized = employee.getUsername().trim().replaceAll("[^a-zA-Z0-9]", "_");
            } else if (employee.getId() != null) {
                empIdSanitized = "EMP" + employee.getId();
            }
        }

        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String ext = ".pdf";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        if (totalFiles > 1) {
            return String.format("%s_%s_%s_v%d_%d%s", deptSanitized, empIdSanitized, dateStr, versionNumber, fileIndex + 1, ext);
        } else {
            return String.format("%s_%s_%s_v%d%s", deptSanitized, empIdSanitized, dateStr, versionNumber, ext);
        }
    }

    @Override
    public Proposal saveProposal(Proposal proposal, MultipartFile[] files) {

        List<String> fileNames = new ArrayList<>();

        try {
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            int versionNumber = 1;
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                String newFileName = generateCustomFileName(proposal, file.getOriginalFilename(), versionNumber, i, files.length);
                fileNames.add(newFileName);

                Path filePath = uploadPath.resolve(newFileName);
                Files.copy(
                        file.getInputStream(),
                        filePath,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String joinedFileNames = String.join(",", fileNames);
        proposal.setFile(joinedFileNames);

        Proposal savedProposal = proposalRepo.save(proposal);

        // Save Version 1 initial revision log
        ProposalRevision v1 = new ProposalRevision();
        v1.setProposal(savedProposal);
        v1.setVersionNumber(1);
        v1.setSubmissionDate(LocalDateTime.now());
        v1.setRemarks("Initial Proposal Submission");
        v1.setFileName(joinedFileNames);
        revisionRepo.save(v1);

        return savedProposal;
    }

    @Override
    public Proposal resubmitProposal(Long proposalId, MultipartFile[] files, String remarks) {
        Proposal proposal = proposalRepo.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found with ID: " + proposalId));

        List<ProposalRevision> existingRevisions = revisionRepo.findByProposalIdOrderByVersionNumberAsc(proposalId);
        int nextVersion = 1;

        if (existingRevisions != null && !existingRevisions.isEmpty()) {
            nextVersion = existingRevisions.get(existingRevisions.size() - 1).getVersionNumber() + 1;
        } else {
            nextVersion = 2;
        }

        List<String> fileNames = new ArrayList<>();
        try {
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                String newFileName = generateCustomFileName(proposal, file.getOriginalFilename(), nextVersion, i, files.length);
                fileNames.add(newFileName);

                Path filePath = uploadPath.resolve(newFileName);
                Files.copy(
                        file.getInputStream(),
                        filePath,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String newFilesStr = String.join(",", fileNames);

        // Append new file(s) to proposal file string for quick access
        if (proposal.getFile() != null && !proposal.getFile().trim().isEmpty()) {
            proposal.setFile(proposal.getFile() + "," + newFilesStr);
        } else {
            proposal.setFile(newFilesStr);
        }

        proposal.setStatus("UNDER_REVIEW");
        proposalRepo.save(proposal);

        if (existingRevisions == null || existingRevisions.isEmpty()) {
            // Retrospectively log Version 1 for legacy proposals created before revision tracking
            ProposalRevision v1 = new ProposalRevision();
            v1.setProposal(proposal);
            v1.setVersionNumber(1);
            v1.setSubmissionDate(LocalDateTime.now().minusHours(1));
            v1.setRemarks("Initial Proposal Submission");
            String[] existingFiles = proposal.getFile().split(",");
            v1.setFileName(existingFiles.length > 0 ? existingFiles[0] : newFilesStr);
            revisionRepo.save(v1);
        }

        ProposalRevision revision = new ProposalRevision();
        revision.setProposal(proposal);
        revision.setVersionNumber(nextVersion);
        revision.setSubmissionDate(LocalDateTime.now());
        revision.setRemarks(remarks != null && !remarks.trim().isEmpty() ? remarks : "Revised proposal document uploaded.");
        revision.setFileName(newFilesStr);
        revisionRepo.save(revision);

        return proposalRepo.findById(proposalId).orElse(proposal);
    }

    public List<Proposal> getAll() {
        return proposalRepo.findAllByOrderByIdDesc();
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
        return proposalRepo.findByEmployee_IdOrderByIdDesc(employeeId);
    }

    @Override
    public List<ProposalReview> getProposalReviews(
            Long proposalId) {

        return reviewRepo.findByProposalId(proposalId);

    }

    @Override
    public List<ProposalRevision> getProposalRevisions(Long proposalId) {
        return revisionRepo.findByProposalIdOrderByVersionNumberAsc(proposalId);
    }
}