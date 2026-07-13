package com.proposal.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    private String username;
    private String password;

}