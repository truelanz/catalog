package com.truelanz.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class UserIsertDTO extends UserDTO{

    @NotBlank(message = "Password is required")
    private String password;
}
