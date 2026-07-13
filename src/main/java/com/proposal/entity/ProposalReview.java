package com.proposal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProposalReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
@JoinColumn(name="proposal_id")
@JsonBackReference
private Proposal proposal;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee reviewer;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    private LocalDateTime reviewDate;
    private String proposalFile;
    
}