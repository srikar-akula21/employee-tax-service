package com.employee.employee_tax.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class Employee {

    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> phoneNumbers;
    private LocalDate doj; // Date of Joining
    private Double salary; // Monthly Salary

    // Constructors
    public Employee() {
    }

    public Employee(String employeeId, String firstName, String lastName, String email, List<String> phoneNumbers, LocalDate doj, Double salary) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumbers = phoneNumbers;
        this.doj = doj;
        this.salary = salary;
    }


    @Override
    public String toString() {
        return "Employee{" +
                "employeeId='" + employeeId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumbers=" + phoneNumbers +
                ", doj=" + doj +
                ", salary=" + salary +
                '}';
    }
}

