package com.employee.employee_tax.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "employees")
@Data
@Getter
public class Employee {

    @Id
    @Column(name = "employee_id", nullable = false, unique = true)
    private String employeeId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @ElementCollection
    @CollectionTable(name = "employee_phone_numbers", joinColumns = @JoinColumn(name = "employee_id"))
    @Column(name = "phone_number")
    private List<String> phoneNumbers;

    @Column(name = "doj", nullable = false)
    private LocalDate doj;

    @Column(name = "salary", nullable = false)
    private Double salary;

    // Getters and Setters
}
