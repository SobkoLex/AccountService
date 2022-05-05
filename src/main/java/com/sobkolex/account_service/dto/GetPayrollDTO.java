package com.sobkolex.account_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPayrollDTO{

    private String name;
    private String lastname;
    private LocalDate period;
    private long salary;

    public String getSalary() {
        return String.format("%d dollar(s) %d cent(s)", salary / 100, salary % 100);
    }

    public String getPeriod() {
        return period.format(DateTimeFormatter.ofPattern("MMMM-yyyy").localizedBy(Locale.CANADA));
    }

}
