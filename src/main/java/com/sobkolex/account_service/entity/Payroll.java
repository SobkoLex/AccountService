package com.sobkolex.account_service.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
@Table(name = "payrolls", uniqueConstraints = @UniqueConstraint(columnNames = {"period", "employee"}))
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private LocalDate period;

    private long salary;

    private String employee;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payroll payment = (Payroll) o;
        return Objects.equals(period, payment.period) &&
                Objects.equals(employee, payment.employee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(period, employee);
    }
}
