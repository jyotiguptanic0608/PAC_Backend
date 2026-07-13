package com.proposal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long roleId;

    private String roleName;

    private String roleAbbreviation;

    private String roleDescription;
}