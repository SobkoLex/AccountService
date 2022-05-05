package com.sobkolex.account_service.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class PutPayrollDTO {

    private String employee;
    private LocalDate period;
    private long salary;

    public void setPeriod(String date) {
        period = LocalDate.parse("01-" + date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
