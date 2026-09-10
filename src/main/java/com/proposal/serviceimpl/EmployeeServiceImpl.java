package com.proposal.serviceimpl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;
import org.springframework.stereotype.Service;

import com.proposal.dto.RegisterResponse;
import com.proposal.entity.Employee;
import com.proposal.repository.EmployeeRepository;
import com.proposal.service.EmployeeService;
import com.proposal.repository.RoleRepository;
import com.proposal.repository.PacMemberRepository;
import com.proposal.entity.Role;
import com.proposal.entity.PacMember;
import jakarta.servlet.http.HttpSession;

import com.proposal.dto.ResetPasswordRequest;
import com.proposal.service.OtpService;
import com.proposal.service.SmsService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repo;
    private final BCryptPasswordEncoder encoder;
    private final RoleRepository roleRepo;
    private final PacMemberRepository pacMemberRepo;
    private final OtpService otpService;
    private final SmsService smsService;

    public EmployeeServiceImpl(EmployeeRepository repo,
                               BCryptPasswordEncoder encoder,
                               RoleRepository roleRepo,
                               PacMemberRepository pacMemberRepo,
                               OtpService otpService,
                               SmsService smsService) {
        this.repo = repo;
        this.encoder = encoder;
        this.roleRepo = roleRepo;
        this.pacMemberRepo = pacMemberRepo;
        this.otpService = otpService;
        this.smsService = smsService;
    }

 @Override
public RegisterResponse register(Employee employee) {

    long count = repo.count() + 1;

    String number = String.format("%03d", count);

    String username = "NIC@PAC" + number;
    String rawPassword = "nic@" + number;

    employee.setUsername(username);
    employee.setPassword(encoder.encode(rawPassword));

    if(employee.isPacCommitteeMember()) {

        Role pacRole =
                roleRepo.findByRoleName("PAC_MEMBER");

        employee.setRole(pacRole);
    }
    else {

        Role empRole =
                roleRepo.findByRoleName("EMPLOYEE");

        employee.setRole(empRole);
    }

    Employee savedEmployee = repo.save(employee);

    if(savedEmployee.isPacCommitteeMember()) {

      PacMember pacMember = new PacMember();

pacMember.setEmployee(employee);

pacMember.setName(employee.getName());
pacMember.setEmail(employee.getEmail());
pacMember.setContactNumber(employee.getContactNumber());
pacMember.setDepartmentName(employee.getDepartmentName());

pacMember.setDesignation(employee.getDesignation());
pacMember.setDivision(employee.getDivision());
pacMember.setStateOfPosting(employee.getStateOfPosting());
pacMember.setPermanentAddress(employee.getPermanentAddress());
pacMember.setIpNumber(employee.getIpNumber());
pacMember.setEmergencyContact(employee.getEmergencyContact());

pacMember.setUsername(employee.getUsername());
pacMember.setPassword(employee.getPassword());

pacMember.setPacDesignation("Member");

pacMemberRepo.save(pacMember);
    }

    return new RegisterResponse(username, rawPassword);
}    @Override
    public Employee login(String username,
                          String password,
                          String captcha,
                          HttpSession session) {

        // CAPTCHA verification disabled for now
        /*
        String actualCaptcha = (String) session.getAttribute("captcha");

        if (actualCaptcha == null || !actualCaptcha.equalsIgnoreCase(captcha)) {
            throw new RuntimeException("Invalid CAPTCHA");
        }

        // Clear used CAPTCHA to prevent replay attacks
        session.removeAttribute("captcha");
        */

        Employee employee = repo.findByUsername(username);

        if (employee == null) {
            return null;
        }

        // Verify incoming raw password against BCrypt hashed password in database
        if (employee.getPassword() != null && encoder.matches(password, employee.getPassword())) {
            return employee;
        }

        return null;
    }
    @Override
    public List<Employee> getAllEmployees() {

        return repo.findAll();
    }
    @Override
    public List<Employee> getPacMembers() {
        return repo.findByPacCommitteeMember(true);
    }

    @Override
    public Employee getEmployee(Long id){
        return repo.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> sendForgotPasswordOtp(String identifier) {
        Map<String, Object> result = new HashMap<>();

        if (identifier == null || identifier.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "Please provide a valid Username or Mobile Number.");
            return result;
        }

        String cleanId = identifier.trim();
        Optional<Employee> empOpt = repo.findByUsernameOrContactNumber(cleanId, cleanId);

        if (empOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "No account found matching the provided Username or Mobile Number.");
            return result;
        }

        Employee employee = empOpt.get();
        String contactNum = employee.getContactNumber();

        if (contactNum == null || contactNum.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "No registered mobile number found for this account. Please contact administrator.");
            return result;
        }

        contactNum = contactNum.trim();
        String otp = otpService.generateOtp(employee.getUsername());

        boolean smsSent = smsService.sendOtpSms(contactNum, otp);

        String maskedPhone = contactNum.length() >= 10
                ? "******" + contactNum.substring(contactNum.length() - 4)
                : contactNum;

        if (smsSent) {
            result.put("success", true);
            result.put("username", employee.getUsername());
            result.put("maskedPhone", maskedPhone);
            result.put("message", "OTP sent successfully to registered mobile ending with " + contactNum.substring(Math.max(0, contactNum.length() - 4)));
        } else {
            result.put("success", false);
            result.put("message", "Failed to send OTP SMS to " + maskedPhone + ". Please try again later.");
        }

        return result;
    }

    @Override
    public Map<String, Object> resetPasswordWithOtp(ResetPasswordRequest request) {
        Map<String, Object> result = new HashMap<>();

        if (request == null || request.getIdentifier() == null || request.getOtp() == null || request.getNewPassword() == null) {
            result.put("success", false);
            result.put("message", "Missing required fields.");
            return result;
        }

        String cleanId = request.getIdentifier().trim();
        Optional<Employee> empOpt = repo.findByUsernameOrContactNumber(cleanId, cleanId);

        if (empOpt.isEmpty()) {
            result.put("success", false);
            result.put("message", "Account not found.");
            return result;
        }

        Employee employee = empOpt.get();

        boolean isValidOtp = otpService.validateOtp(employee.getUsername(), request.getOtp());
        if (!isValidOtp) {
            result.put("success", false);
            result.put("message", "Invalid or expired OTP. Please verify the code or request a new OTP.");
            return result;
        }

        String encodedPassword = encoder.encode(request.getNewPassword());
        employee.setPassword(encodedPassword);
        repo.save(employee);

        pacMemberRepo.findByEmployeeId(employee.getId()).ifPresent(pac -> {
            pac.setPassword(encodedPassword);
            pacMemberRepo.save(pac);
        });

        otpService.clearOtp(employee.getUsername());

        result.put("success", true);
        result.put("message", "Password has been reset successfully. Please log in with your new password.");
        return result;
    }
}