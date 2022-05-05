package com.sobkolex.account_service.securety;


import com.sobkolex.account_service.services.EventService;
import com.sobkolex.account_service.services.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletResponse;

import static com.sobkolex.account_service.model.Permission.*;
import static com.sobkolex.account_service.model.SecurityEvent.ACCESS_DENIED;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final UserDetailService userDetailsService;
    private final EventService eventService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers(HttpMethod.POST, "/actuator/shutdown").permitAll()
                .mvcMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                .mvcMatchers(HttpMethod.GET, "/api/empl/payment").hasAuthority(GET_PAYMENTS.name())
                .mvcMatchers(HttpMethod.POST,"/api/acct/payments").hasAuthority(EDIT_PAYMENTS.name())
                .mvcMatchers(HttpMethod.GET, "/api/admin/user/**").hasAnyAuthority(GET_USERS_INFO.name())
                .mvcMatchers(HttpMethod.DELETE, "/api/admin/user/**").hasAuthority(DELETE_USER.name())
                .mvcMatchers(HttpMethod.PUT,"/api/admin/user/**").hasAuthority(CHANGE_ROLE.name())
                .mvcMatchers(HttpMethod.GET, "/api/security/events").hasAuthority(GET_EVENTS.name())
                .anyRequest().authenticated()
                .and()
                .httpBasic().authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler())
                .and()
                .csrf().disable().headers().frameOptions().disable()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().
                withUser("admin")
                .password(getEncoder().encode("somePassword"))
                .roles("ADMINISTRATOR")
                .and()
                .passwordEncoder(getEncoder());

        auth.userDetailsService(userDetailsService)
                .passwordEncoder(getEncoder());
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder(15);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return ((request, response, accessDeniedException) -> {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String path = request.getServletPath();
            this.eventService.save(ACCESS_DENIED, username ,path , path);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied!");
        });
    }

    @Autowired
    public WebSecurityConfig(RestAuthenticationEntryPoint restAuthenticationEntryPoint, UserDetailService userDetailsService, EventService eventService) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.eventService = eventService;
        this.userDetailsService = userDetailsService;
    }
}
