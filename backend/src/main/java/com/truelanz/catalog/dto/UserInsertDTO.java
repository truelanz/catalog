package com.truelanz.catalog.dto;

import com.truelanz.catalog.services.validation.UserInsertValid;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@UserInsertValid //Custon annotation
public class UserInsertDTO extends UserDTO{

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Minimum 8 characters required")
    //@Pattern(regexp = "")
    private String password;
}
