package com.truelanz.catalog.dto;

import com.truelanz.catalog.services.validation.UserInsertValid;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@UserInsertValid //Custon annotation
public class UserInsertDTO extends UserDTO{

    @NotBlank(message = "Password is required")
    private String password;
}
