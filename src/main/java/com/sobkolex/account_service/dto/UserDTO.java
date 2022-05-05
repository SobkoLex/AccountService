package com.sobkolex.account_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sobkolex.account_service.entity.User;
import com.sobkolex.account_service.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Data
@Component
@NoArgsConstructor
public class UserDTO {

    private long id;

    private String name;

    private String lastname;

    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private List<Role> roles;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.roles = user.getRoles();
    }
}
