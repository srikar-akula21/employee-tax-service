package com.employee.employee_tax.service;

import com.employee.employee_tax.dto.EmployeeDTO;
import com.employee.employee_tax.dto.EmployeeRepository;
import com.employee.employee_tax.entity.Employee;
import com.employee.employee_tax.exception.ValidationException;
import com.employee.employee_tax.model.TaxDeduction;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee saveEmployee(EmployeeDTO employeeDTO) {
        Map<String, String> errors = new HashMap<>();

        if (employeeDTO.getEmployeeId() == null || employeeDTO.getEmployeeId().trim().isEmpty()) {
            errors.put("employeeId", "Employee ID is mandatory");
        }

        if (employeeDTO.getFirstName() == null || employeeDTO.getFirstName().trim().isEmpty()) {
            errors.put("firstName", "First name is mandatory");
        }

        if (employeeDTO.getLastName() == null || employeeDTO.getLastName().trim().isEmpty()) {
            errors.put("lastName", "Last name is mandatory");
        }

        if (employeeDTO.getEmail() == null || employeeDTO.getEmail().trim().isEmpty()) {
            errors.put("email", "Email is mandatory");
        } else if (!employeeDTO.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errors.put("email", "Invalid email format");
        }

        if (employeeDTO.getPhoneNumbers() == null || employeeDTO.getPhoneNumbers().isEmpty()) {
            errors.put("phoneNumbers", "At least one phone number is required");
        } else if (!employeeDTO.getPhoneNumbers().stream().allMatch(phone -> phone.matches("\\d{10}"))) {
            errors.put("phoneNumbers", "Each phone number must be 10 digits");
        }

        if (employeeDTO.getDoj() == null) {
            errors.put("doj", "Date of joining is mandatory");
        }

        if (employeeDTO.getSalary() == null || employeeDTO.getSalary() <= 0) {
            errors.put("salary", "Salary must be a positive number");
        }

        // If there are errors, throw a ValidationException
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        return employeeRepository.save(employee);
    }

    /*public TaxDeduction calculateTax(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
        double yearlySalary = employee.getSalary() * 12;

        double tax = 0;
        double remaining = yearlySalary;

        if (remaining > 250000) {
            double taxable = Math.min(remaining - 250000, 250000);
            tax += taxable * 0.05;
            remaining -= taxable;
        }
        if (remaining > 500000) {
            double taxable = Math.min(remaining - 500000, 500000);
            tax += taxable * 0.10;
            remaining -= taxable;
        }
        if (remaining > 1000000) {
            tax += (remaining - 1000000) * 0.20;
        }

        double cess = yearlySalary > 2500000 ? (yearlySalary - 2500000) * 0.02 : 0;

        return new TaxDeduction(employee.getEmployeeId(), employee.getFirstName(),
                employee.getLastName(), yearlySalary, tax, cess);
    }*/

    public TaxDeduction calculateTax(String empId) {
        // Retrieve employee details from repository
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        // Extract details
        String firstName = employee.getFirstName();
        String lastName = employee.getLastName();
        double monthlySalary = employee.getSalary();
        LocalDate dateOfJoining = employee.getDoj();

        // See if doj is before this financial year
        LocalDate financialYearStart = LocalDate.of(LocalDate.now().getYear(), Month.APRIL, 1);
        LocalDate financialYearEnd = LocalDate.of(LocalDate.now().getYear() + 1, Month.MARCH, 31);

        // If not calculate the total months
        LocalDate dojEffective = dateOfJoining.isBefore(financialYearStart) ? financialYearStart : dateOfJoining;
        long monthsWorked = ChronoUnit.MONTHS.between(dojEffective.withDayOfMonth(1), financialYearEnd.withDayOfMonth(1)) + 1;

        // Calculate yearly effective salary based on num of months the employee worked in current financial year
        double yearlySalary = monthlySalary * monthsWorked;

        // Calculate tax based on slabs
        double taxAmount = calculateTaxAmount(yearlySalary);

        // Calculate cess if applicable
        double cessAmount = yearlySalary > 2_500_000 ? 0.02 * (yearlySalary - 2_500_000) : 0;

        // Build the response object
        return new TaxDeduction(empId, firstName, lastName, yearlySalary, taxAmount, cessAmount);
    }

    private double calculateTaxAmount(double yearlySalary) {
        double tax = 0;

        if (yearlySalary > 250_000 && yearlySalary <= 500_000) {
            tax += 0.05 * (yearlySalary - 250_000);
        } else if (yearlySalary > 500_000 && yearlySalary <= 1_000_000) {
            tax += 0.05 * 250_000; // First 250,000 in this slab
            tax += 0.10 * (yearlySalary - 500_000);
        } else if (yearlySalary > 1_000_000) {
            tax += 0.05 * 250_000; // First 250,000 in this slab
            tax += 0.10 * 500_000; // Next 500,000 in this slab
            tax += 0.20 * (yearlySalary - 1_000_000);
        }

        return tax;
    }
}

