package com.sobkolex.account_service.controllers;

import com.sobkolex.account_service.dto.PutPayrollDTO;
import com.sobkolex.account_service.entity.User;
import com.sobkolex.account_service.services.EventService;
import com.sobkolex.account_service.services.PayRollService;
import com.sobkolex.account_service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@RestController
public class BusinessApiController {

    private final UserService userService;
    private final PayRollService payRollService;
    private final EventService eventService;


    @GetMapping("/api/empl/payment")
    public ResponseEntity<?> getPayroll(@RequestParam(required = false) String period,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findUserByEmail(userDetails.getUsername());
        if (period != null)
            return new ResponseEntity<>(payRollService.getPayrollByPeriodAndUser(period, user), HttpStatus.OK);
        else
            return new ResponseEntity<>(payRollService.getPayrollsByUser(user), HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/api/acct/payments")
    public ResponseEntity<?> addPayrolls(@RequestBody List<PutPayrollDTO> payrolls) {
        payRollService.addPayrolls(payrolls);
        return new ResponseEntity<>(Map.of("status", "Added successfully!"), HttpStatus.OK);
    }

    @Transactional
    @PutMapping("/api/acct/payments")
    public ResponseEntity<?> updatePaymentInfo(@RequestBody PutPayrollDTO payrollInfo) {
        payRollService.updatePayroll(payrollInfo);
        return new ResponseEntity<>(Map.of("status", "Updated successfully!"), HttpStatus.OK);
    }

    @GetMapping("api/security/events")
    public ResponseEntity<?> getEvents() {
        return new ResponseEntity<>(eventService.findAllEvents(), HttpStatus.OK);
    }

    @Autowired
    public BusinessApiController(UserService userService, PayRollService payRollService, EventService eventService) {
        this.userService = userService;
        this.payRollService = payRollService;
        this.eventService = eventService;
    }
}
