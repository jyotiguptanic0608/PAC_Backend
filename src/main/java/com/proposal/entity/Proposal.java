package com.proposal.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;
@Entity
@Table(name = "proposals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String groupHeadName;
    private String projectCoordinator;
    private String departmentName;
    private String title;
    private String description;
    private String date;
    private String file;
    private String status;
    @OneToMany(mappedBy = "proposal", cascade = CascadeType.ALL)
@JsonManagedReference
private List<ProposalReview> reviews;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="employee_id")
    private Employee employee;
    @JsonProperty("employeeName")
public String getEmployeeName() {
    return employee != null ? employee.getName() : "";
}
}