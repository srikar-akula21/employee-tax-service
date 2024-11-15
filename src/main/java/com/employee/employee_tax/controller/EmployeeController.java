package com.employee.employee_tax.controller;

import com.employee.employee_tax.dto.EmployeeDTO;
import com.employee.employee_tax.entity.Employee;
import com.employee.employee_tax.model.TaxDeduction;
import com.employee.employee_tax.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        Employee employee = employeeService.saveEmployee(employeeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    @GetMapping("/{employeeId}/tax-deductions")
    public ResponseEntity<TaxDeduction> getTaxDeductions(@PathVariable String employeeId) {
        TaxDeduction taxDeduction = employeeService.calculateTax(employeeId);
        return ResponseEntity.ok(taxDeduction);
    }
}

