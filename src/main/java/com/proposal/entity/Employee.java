package com.proposal.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;

    private String name;
    private String email;
    private String contactNumber;
    private String departmentName;
    private String designation;

    private String division;

    private String stateOfPosting;

    @Column(length = 500)
    private String permanentAddress;

    private String ipNumber;

    private String emergencyContact;

    private String username;
  @JsonIgnore
private String password;

    @Column(nullable = false)
    private boolean pacCommitteeMember = false;

    @Column(name = "group_head", nullable = false)
    private boolean groupHead = false;

    @OneToMany(mappedBy="employee")
    private List<Proposal> proposals;
    @Column(nullable = false)
    private boolean chairman = false;
}
