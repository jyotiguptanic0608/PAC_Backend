package com.proposal.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="pac_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="employee_id")
    private Employee employee;

    private String name;
    private String email;
    private String contactNumber;
    private String departmentName;
    private String designation;
    private String division;
    private String stateOfPosting;
    private String permanentAddress;
    private String ipNumber;
    private String emergencyContact;
    private String username;

    @JsonIgnore
    private String password;

    private String pacDesignation;
}