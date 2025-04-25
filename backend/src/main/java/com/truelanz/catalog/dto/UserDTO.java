package com.truelanz.catalog.dto;

import java.util.HashSet;
import java.util.Set;

import com.truelanz.catalog.entities.Role;
import com.truelanz.catalog.entities.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    @NotBlank(message = "Required field")
    private String firstName;
    @NotBlank(message = "Required field")
    private String lastName;
    @Email(message = "Not a valid email")
    private String email;

    @Setter(AccessLevel.NONE)
    Set<RoleDTO> roles = new HashSet<>();

    public UserDTO(User entity, Set<Role> roles) {
        this(entity);
        roles.forEach(role -> this.roles.add(new RoleDTO(role)));
    }

    public UserDTO(User entity) {
        id = entity.getId();
        firstName = entity.getFirstName();
        lastName = entity.getLastName();
        email = entity.getEmail();
        //Pegar lista de RoleDTO e inserir nos usuários
        entity.getRoles().forEach(role -> this.roles.add(new RoleDTO(role))); 
    }
    
}
