package com.sobkolex.account_service.securety;


import com.sobkolex.account_service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private final UserService userService;
    private final WebRequest request;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();
        String path = request.getDescription(false).substring(4);

        userService.increaseFailedAttempts(username, path);
    }

    @Autowired
    public AuthenticationFailureListener(UserService userService, WebRequest request) {
        this.userService = userService;
        this.request = request;
    }
}