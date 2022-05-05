package com.sobkolex.account_service.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.sobkolex.account_service.model.Permission.*;

public enum Role {
    ROLE_ADMINISTRATOR(Set.of(CHANGE_ROLE, GET_USERS_INFO, CHANGE_PASSWORD, DELETE_USER)),
    ROLE_USER(Set.of(CHANGE_PASSWORD, GET_PAYMENTS)),
    ROLE_ACCOUNTANT(Set.of(CHANGE_PASSWORD, GET_PAYMENTS, ADD_PAYMENTS, EDIT_PAYMENTS, CHANGE_STATUS)),
    ROLE_AUDITOR(Set.of(GET_EVENTS));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<GrantedAuthority> getAuthorities() {
        return getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toSet());
    }
}


