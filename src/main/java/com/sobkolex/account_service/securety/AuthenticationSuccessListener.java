package com.sobkolex.account_service.securety;

import com.sobkolex.account_service.entity.User;
import com.sobkolex.account_service.repositories.UserRepository;
import com.sobkolex.account_service.services.EventService;
import com.sobkolex.account_service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final UserService userService;
    private final WebRequest request;
    private final EventService eventService;
    private final UserRepository userRepository;

    @Autowired
    public AuthenticationSuccessListener(UserService userService, WebRequest request, EventService eventService, UserRepository userRepository) {
        this.userService = userService;
        this.request = request;
        this.eventService = eventService;
        this.userRepository = userRepository;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        String path = request.getDescription(false).substring(4);
        User user = userService.findUserByEmail(username);

        user.setFailedAttempt(0);
        userRepository.save(user);

        if (!user.isAccountNonLocked())
            userService.unlock(user, username, path);
    }
}