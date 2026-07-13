package com.proposal.service;
import com.proposal.entity.PacMember;
import com.proposal.entity.Employee;
import java.util.List;

public interface AdminService {
List<PacMember> getPacMembers();

void updatePacMembers(List<Long> ids);
void makeChairman(Long employeeId);

Employee removePacMember(Long employeeId);
Employee removeChairman(Long employeeId);
}