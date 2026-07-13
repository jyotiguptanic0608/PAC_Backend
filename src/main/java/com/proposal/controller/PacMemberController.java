package com.proposal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.proposal.entity.PacMember;
import com.proposal.service.PacMemberService;

@RestController
@RequestMapping("/api/pac")
@CrossOrigin(origins = "*")
public class PacMemberController {

    private final PacMemberService pacMemberService;

    public PacMemberController(PacMemberService pacMemberService) {

        this.pacMemberService = pacMemberService;

    }

    @GetMapping("/{employeeId}")
    public PacMember getPacMember(
            @PathVariable Long employeeId) {

        return pacMemberService.getPacMember(employeeId);

    }

    @GetMapping
    public List<PacMember> getAllPacMembers() {

        return pacMemberService.getAllPacMembers();

    }

}