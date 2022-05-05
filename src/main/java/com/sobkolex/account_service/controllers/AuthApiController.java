package com.sobkolex.account_service.controllers;

import com.sobkolex.account_service.dto.NewPassword;
import com.sobkolex.account_service.dto.UserDTO;
import com.sobkolex.account_service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private UserService userService;

    @PostMapping("/signup")
    public UserDTO register(@Valid @RequestBody UserDTO user) {
        return userService.createUser(user);
    }

    @PostMapping("/changepass")
    public ResponseEntity<?> changePassword(@RequestBody NewPassword new_password,
                               @AuthenticationPrincipal UserDetails user) {

        userService.changePassword(new_password, user);

        return new ResponseEntity<>(Map.of("email", user.getUsername()
        ,"status","The password has been updated successfully"),HttpStatus.OK);
    }

    @Autowired
    public AuthApiController(UserService userService) {
        this.userService = userService;
    }
}