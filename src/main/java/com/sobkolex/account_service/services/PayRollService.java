package com.sobkolex.account_service.services;

import com.sobkolex.account_service.dto.GetPayrollDTO;
import com.sobkolex.account_service.dto.PutPayrollDTO;
import com.sobkolex.account_service.entity.Payroll;
import com.sobkolex.account_service.entity.User;
import com.sobkolex.account_service.exseption.NotFoundException;
import com.sobkolex.account_service.repositories.PayrollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PayRollService {

    private final UserService userService;
    private final PayrollRepository payrollRepository;


    public void updatePayroll(PutPayrollDTO payrollDTO) {
        User user = userService.findUserByEmail(payrollDTO.getEmployee());
        Payroll payroll = payrollRepository.findByEmployeeAndPeriod(user.getEmail(), payrollDTO.getPeriod());

        if (payroll == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payroll not found!");

        payroll.setSalary(payrollDTO.getSalary());
        payrollRepository.save(payroll);
    }

    public void savePayroll(Payroll payroll) {
        payrollRepository.save(payroll);
    }

    public void addPayrolls(List<PutPayrollDTO> payrolls) {
        List<User> users = payrolls.stream().map(PutPayrollDTO::getEmployee)
                .map(userService::findUserByEmail).collect(Collectors.toList());

        List<Payroll> list = payrolls.stream().map(this::convertToPayroll).collect(Collectors.toList());

        List<Payroll> allRecords = users.stream().
                flatMap(o -> payrollRepository.findAllByEmployee(o.getEmail()).stream())
                .collect(Collectors.toList());
        allRecords.addAll(list);

        boolean isPresent = allRecords.stream().distinct().count() != allRecords.size();

        if (isPresent) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The payroll must be unique for each employee!");
        } else if (list.stream().filter(o -> o.getSalary() < 0).count() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The salary can't be negative!");
        }

        list.forEach(this::savePayroll);
    }

    public List<GetPayrollDTO> getPayrollsByUser(User user) {
        List<Payroll> result = payrollRepository.findAllByEmployee(user.getEmail());
        if (result == null)
            throw new NotFoundException("Payroll not found!");
        else
            return result.stream()
                    .sorted(Comparator.comparing(Payroll::getPeriod).reversed())
                    .map(this::convertToGetPayrollDTO)
                    .collect(Collectors.toList());
    }

    public GetPayrollDTO getPayrollByPeriodAndUser(String period, User user) {
        try {
            LocalDate date = LocalDate.parse("01-" + period, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            GetPayrollDTO payrollDTO = convertToGetPayrollDTO(payrollRepository.findByEmployeeAndPeriod(user.getEmail(), date));
            if (payrollDTO == null)
                throw new NotFoundException("Payroll not found!");
            else
                return payrollDTO;
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong period format!");
        }
    }


    private Payroll convertToPayroll(PutPayrollDTO dto) {
        Payroll payroll = new Payroll();
        payroll.setSalary(dto.getSalary());
        payroll.setPeriod(dto.getPeriod());
        payroll.setEmployee(userService.findUserByEmail(dto.getEmployee()).getEmail());
        return payroll;
    }

    private GetPayrollDTO convertToGetPayrollDTO(Payroll payroll) {
        GetPayrollDTO dto = new GetPayrollDTO();
        dto.setSalary(payroll.getSalary());
        dto.setPeriod(payroll.getPeriod());
        dto.setName(payroll.getEmployee());
        dto.setLastname(payroll.getEmployee());
        return dto;
    }

    @Autowired
    public PayRollService(UserService userService, PayrollRepository payrollRepository) {
        this.userService = userService;
        this.payrollRepository = payrollRepository;
    }
}
