package com.proposal.controller;

import com.proposal.dto.ReviewRequest;
import com.proposal.entity.Proposal;
import com.proposal.service.ProposalService;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.net.MalformedURLException;
import com.proposal.repository.EmployeeRepository;
import com.proposal.entity.Employee;
import com.proposal.entity.ProposalReview;
import com.proposal.entity.ProposalRevision;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/proposals")
@CrossOrigin(
    origins = "http://localhost:5173",
    allowCredentials = "true"
)
public class ProposalController {

    private final ProposalService service;
    private final EmployeeRepository employeeRepo;

    @Value("${file.upload-dir:D:/PAC_Proposals}")
    private String uploadDir;

    public ProposalController(
            ProposalService service,
            EmployeeRepository employeeRepo) {

        this.service = service;
        this.employeeRepo = employeeRepo;
    }

    @GetMapping
    public List<Proposal> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Proposal saveProposal(
            @RequestParam("employeeId") Long employeeId,
            @RequestParam("departmentName") String departmentName,
            @RequestParam("groupHeadName") String groupHeadName,
            @RequestParam("projectCoordinator") String projectCoordinator,
            @RequestParam("title") String title,
            @RequestParam("date") String date,
            @RequestParam("description") String description,
            @RequestParam("files") MultipartFile[] files) {

        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Proposal proposal = new Proposal();

        proposal.setEmployee(employee);
        proposal.setDepartmentName(departmentName);
        proposal.setGroupHeadName(groupHeadName);
        proposal.setProjectCoordinator(projectCoordinator);
        proposal.setTitle(title);
        proposal.setDate(date);
        proposal.setDescription(description);

        proposal.setStatus("UNDER_REVIEW");

        return service.saveProposal(proposal, files);
    }

    @PostMapping("/{id}/resubmit")
    public Proposal resubmitProposal(
            @PathVariable Long id,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "remarks", required = false) String remarks) {

        return service.resubmitProposal(id, files, remarks);
    }

    @GetMapping("/employee/{employeeId}")
    public List<Proposal> getEmployeeProposals(
            @PathVariable Long employeeId) {

        return service.getEmployeeProposals(employeeId);

    }

    @PutMapping("/{id}/review")
    public void reviewProposal(
            @PathVariable Long id,
            @RequestBody ReviewRequest request) {

        Long reviewerId = request.getReviewerId();

        service.reviewProposal(
                id,
                reviewerId,
                request.getReview());
    }

    @PutMapping("/{id}/approve")
    public Proposal approveProposal(
            @PathVariable Long id) {

        return service.approveProposal(id);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadProposal(
            @PathVariable Long id) {

        Proposal proposal =
                service.getProposalById(id);

        String fileName = proposal.getFile();
        if (fileName != null && fileName.contains(",")) {
            fileName = fileName.split(",")[0];
        }

        Path path = Paths.get(uploadDir).resolve(fileName != null ? fileName : "");

        Resource resource;

        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to load file resource", e);
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/{id}")
    public Proposal getProposal(
            @PathVariable Long id) {
        return service.getProposalById(id);
    }

    @GetMapping("/{id}/reviews")
    public List<ProposalReview> getProposalReviews(
            @PathVariable Long id) {

        return service.getProposalReviews(id);
    }

    @GetMapping("/{id}/revisions")
    public List<ProposalRevision> getProposalRevisions(
            @PathVariable Long id) {

        return service.getProposalRevisions(id);
    }
}