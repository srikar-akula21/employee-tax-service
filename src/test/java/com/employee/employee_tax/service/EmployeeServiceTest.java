package com.employee.employee_tax.service;


import com.employee.employee_tax.dto.EmployeeDTO;
import com.employee.employee_tax.dto.EmployeeRepository;
import com.employee.employee_tax.entity.Employee;
import com.employee.employee_tax.exception.ValidationException;
import com.employee.employee_tax.model.TaxDeduction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveEmployee_validEmployeeDTO_shouldSaveAndReturnEmployee() {

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeId("E123");
        employeeDTO.setFirstName("John");
        employeeDTO.setLastName("Doe");
        employeeDTO.setEmail("john.doe@example.com");
        employeeDTO.setPhoneNumbers(List.of("1234567890"));
        employeeDTO.setDoj(LocalDate.now().minusMonths(6));
        employeeDTO.setSalary(50000.0);

        Employee savedEmployee = new Employee();
        savedEmployee.setEmployeeId("E123");

        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        Employee result = employeeService.saveEmployee(employeeDTO);

        assertNotNull(result);
        assertEquals("E123", result.getEmployeeId());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void saveEmployee_invalidEmployeeDTO_shouldThrowValidationException() {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeId(""); // Invalid

        ValidationException exception = assertThrows(ValidationException.class, () -> employeeService.saveEmployee(employeeDTO));
        assertTrue(exception.getErrors().containsKey("employeeId"));
    }

    @Test
    void calculateTax_validEmployeeId_shouldReturnTaxDeduction() {

        Employee employee = new Employee();
        employee.setEmployeeId("E123");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setSalary(50000.0);
        employee.setDoj(LocalDate.of(2023, 4, 1));

        when(employeeRepository.findById("E123")).thenReturn(Optional.of(employee));

        TaxDeduction result = employeeService.calculateTax("E123");

        assertNotNull(result);
        assertEquals("E123", result.getEmployeeId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        verify(employeeRepository, times(1)).findById("E123");
    }

    @Test
    void calculateTax_invalidEmployeeId_shouldThrowEntityNotFoundException() {
        when(employeeRepository.findById("E999")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> employeeService.calculateTax("E999"));
        assertEquals("Employee not found", exception.getMessage());
        verify(employeeRepository, times(1)).findById("E999");
    }

    @Test
    void calculateTax_salaryExceedingThreshold_shouldIncludeCess() {
        Employee employee = new Employee();
        employee.setEmployeeId("E124");
        employee.setFirstName("Jane");
        employee.setLastName("Smith");
        employee.setSalary(300000.0);
        employee.setDoj(LocalDate.of(2023, 4, 1));

        when(employeeRepository.findById("E124")).thenReturn(Optional.of(employee));

        TaxDeduction result = employeeService.calculateTax("E124");
        assertNotNull(result);
        assertEquals(3600000.0, result.getYearlySalary());
        assertTrue(result.getCessAmount() > 0);
    }
}

