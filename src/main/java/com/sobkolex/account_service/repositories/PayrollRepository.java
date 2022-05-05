package com.sobkolex.account_service.repositories;

import com.sobkolex.account_service.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    List<Payroll> findAllByEmployee(String employee);

    Payroll findByEmployeeAndPeriod(String employee, LocalDate period);
}
