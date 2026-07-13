package com.proposal.controller;

import java.util.List;
import com.proposal.entity.PacMember;
import org.springframework.web.bind.annotation.*;

import com.proposal.entity.Employee;
import com.proposal.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(
    origins = "http://localhost:5173",
    allowCredentials = "true"
)
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }

  @GetMapping("/pac-members")
public List<PacMember> getPacMembers() {
    return service.getPacMembers();
}
@PutMapping("/chairman/{id}")
public void makeChairman(@PathVariable Long id){

    service.makeChairman(id);

}

@PostMapping("/pac-members")
public void updatePacMembers(
        @RequestBody List<Long> ids){

    service.updatePacMembers(ids);

}

@PutMapping("/remove-pac/{id}")
public Employee removePacMember(
        @PathVariable Long id){

    return service.removePacMember(id);

}
@PutMapping("/remove-chairman/{id}")
public Employee removeChairman(
        @PathVariable Long id){

    return service.removeChairman(id);

}

}