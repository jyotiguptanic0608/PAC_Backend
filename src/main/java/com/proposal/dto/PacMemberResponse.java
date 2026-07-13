package com.proposal.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacMemberResponse {

    private Long id;
    private String name;
    private String email;
    private String contactNumber;
    private String departmentName;
}