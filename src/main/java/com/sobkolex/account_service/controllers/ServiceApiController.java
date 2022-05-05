package com.sobkolex.account_service.controllers;

import com.sobkolex.account_service.dto.UserDTO;
import com.sobkolex.account_service.entity.User;
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
@RequestMapping("/api/admin/user")
public class ServiceApiController {

    private UserService userService;

    @PutMapping("/role")
    public ResponseEntity<?> changeRole(@RequestBody Map<String, String> data,
                                        @AuthenticationPrincipal UserDetails admin) {
        User user = userService.findUserByEmail(data.get("user").toLowerCase());

        user = userService.changeUserRole(data, user, admin.getUsername());

        System.out.println(user);

        return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping("/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email, @AuthenticationPrincipal UserDetails admin) {
        userService.deleteUser(email, admin.getUsername());
        return  new ResponseEntity<>(Map.of("user", email, "status", "Deleted successfully!"), HttpStatus.OK);
    }

    @GetMapping("/")
    public List<UserDTO> getAllUsersInfo(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println(userDetails);
        return userService.getAllUsers();
    }

    @PutMapping("/access")
    public ResponseEntity<?> lockUnlockUser(@RequestBody Map<String, String> data, @AuthenticationPrincipal UserDetails admin) {
        String status = userService.changeUserStatus(data, admin.getUsername());

        return new ResponseEntity<>(Map.of("status", status), HttpStatus.OK);
    }

    @Autowired
    public ServiceApiController(UserService userService) {
        this.userService = userService;
    }
}
