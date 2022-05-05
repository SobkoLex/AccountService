package com.sobkolex.account_service.services;

import com.sobkolex.account_service.dto.NewPassword;
import com.sobkolex.account_service.dto.UserDTO;
import com.sobkolex.account_service.entity.User;
import com.sobkolex.account_service.exseption.CantRemoveAdminException;
import com.sobkolex.account_service.exseption.NotFoundException;
import com.sobkolex.account_service.exseption.UserAlreadyExistException;
import com.sobkolex.account_service.model.Role;
import com.sobkolex.account_service.repositories.BreachedPasswordsRepositories;
import com.sobkolex.account_service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sobkolex.account_service.model.SecurityEvent.*;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final EventService eventService;
    private final PasswordEncoder encoder;
    private final BreachedPasswordsRepositories passwords;
    public static final int MAX_FAILED_ATTEMPTS = 5;

    public UserDTO createUser(UserDTO userData) {
        if (userRepository.findByEmail(userData.getEmail().toLowerCase()) != null)
            throw new UserAlreadyExistException();

        User user = new User();
        passwordValidator(userData.getPassword(), "");
        user.setName(userData.getName());
        user.setLastname(userData.getLastname());
        user.setEmail(userData.getEmail().toLowerCase());
        user.setPassword(encoder.encode(userData.getPassword()));
        user.setRoles(new ArrayList<>());
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);
        user.addRole(Role.ROLE_USER);

        eventService.save(CREATE_USER, null, user.getEmail(), "/api/auth/signup");
        userRepository.findAll().forEach(System.out::println);
        System.out.println(user + "create");

        return new UserDTO(userRepository.save(user));
    }

    public void deleteUser(String email, String admin) {
        User user = findUserByEmail(email);

        boolean isAdmin = user.getRoles().contains(Role.ROLE_ADMINISTRATOR);
        if (isAdmin)
            throw new CantRemoveAdminException();

        userRepository.deleteByEmail(email);
        eventService.save(DELETE_USER, admin, user.getEmail(), "/api/admin/user");
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(UserDTO::new).collect(Collectors.toList());
    }

    public void changePassword(NewPassword new_password, UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername());
        String oldPassword = userDetails.getPassword();

        passwordValidator(new_password.getNew_password(),oldPassword);
        user.setPassword(encoder.encode(new_password.getNew_password()));

        eventService.save(CHANGE_PASSWORD, user.getEmail(), user.getEmail(), "/api/auth/changepass");

        userRepository.save(user);
    }

    public User findUserByEmail(String email) {
        User user  = userRepository.findByEmail(email);
        if (user == null)
            throw new NotFoundException("User not found!");
        return user;
    }

    public String changeUserStatus(Map<String, String> data, String admin) {
        String email = data.get("user").toLowerCase();
        String operation = data.get("operation");

        User user = findUserByEmail(email);

        if (user.hasRole(Role.ROLE_ADMINISTRATOR))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");

        if (operation.equals("UNLOCK"))
            unlock(user, admin, "/api/admin/user/role");
        else if (operation.equals("LOCK"))
            lock(user, admin, "/api/admin/user/role");
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operation must be \"LOCK\" or \"UNLOCK\"");

        userRepository.save(user);
        return String.format("User %s %s!", user.getEmail(), operation.toLowerCase() + "ed");
    }

    public User changeUserRole(Map<String, String> data, User user, String admin) {

        String operation = data.get("operation");
        String roleData = data.get("role");

        boolean isExist = Arrays.stream(Role.values()).map(Role::name).anyMatch(o -> o.equals("ROLE_" + roleData));
        if (!isExist)
            throw new NotFoundException("Role not found!");
        else if (operation == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operation can't be null!");

        Role role = Role.valueOf("ROLE_" + roleData);

        if (operation.equals("GRANT")) {
            grantRole(role, user);
            String subject = String.format("Grant role %s to %s", roleData, user.getEmail());
            eventService.save(GRANT_ROLE, admin, subject, "/api/admin/user/role");
        } else if (operation.equals("REMOVE")) {
            removeRole(role, user);
            String subject = String.format("Remove role %s from %s", roleData, user.getEmail());
            eventService.save(REMOVE_ROLE, admin, subject, "/api/admin/user/role");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operation must be \"GRANT\" or \"REMOVE\"");
        }

        return userRepository.save(user);
    }

    private void removeRole(Role role, User user) {
        if (!user.hasRole(role))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
        else if (role.equals(Role.ROLE_ADMINISTRATOR))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        else if (user.getRoles().size() == 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
        user.removeRole(role);
    }

    private void grantRole(Role role, User user) {
        if ((user.hasRole(Role.ROLE_ADMINISTRATOR) && (role.equals(Role.ROLE_USER) || role.equals(Role.ROLE_AUDITOR))) ||
                ((user.hasRole(Role.ROLE_USER) || user.hasRole(Role.ROLE_AUDITOR)) && role.equals(Role.ROLE_ADMINISTRATOR)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");

        user.addRole(role);
    }

    private void passwordValidator(String newPassword, String oldPassword) {
        String message = null;
        if (newPassword == null)
            message = "The password can't be empty!";
        else if (newPassword.length() < 12)
            message = "Password length must be 12 chars minimum!";
        else if (encoder.matches(newPassword, oldPassword))
            message = "The passwords must be different!";
        else if (passwords.getBreachedPasswords().contains(newPassword))
            message = "The password is in the hacker's database!";

        if (message != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    public void lock(User user, String cause, String path) {
        user.setAccountNonLocked(false);
        userRepository.save(user);
        eventService.save(LOCK_USER, cause,"Lock user " + user.getEmail(), path);
    }

    public void unlock(User user, String admin, String path){
        user.setFailedAttempt(0);
        user.setAccountNonLocked(true);
        userRepository.save(user);
        eventService.save(UNLOCK_USER, admin,"Unlock user " + user.getEmail(), path);
    }

    public void increaseFailedAttempts(String userName, String path) {
        User user = null;
        int failedAttempts = 0;
        if (userRepository.findByEmail(userName) != null) {
            user = findUserByEmail(userName);
            failedAttempts = user.getFailedAttempt() + 1;
            user.setFailedAttempt(failedAttempts);
            userRepository.save(user);
        }
        eventService.save(LOGIN_FAILED, userName ,path , path);
        if (failedAttempts >= MAX_FAILED_ATTEMPTS && !user.getRoles().contains(Role.ROLE_ADMINISTRATOR)) {
            eventService.save(BRUTE_FORCE, user.getEmail(), path, path);
            lock(user, user.getEmail(), path);
        }

    }

    @Autowired
    public UserService(UserRepository userRepository, EventService eventService, PasswordEncoder encoder, BreachedPasswordsRepositories passwords) {
        this.userRepository = userRepository;
        this.eventService = eventService;
        this.encoder = encoder;
        this.passwords = passwords;
    }
}
