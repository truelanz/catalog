package com.truelanz.catalog.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.truelanz.catalog.controllers.handlers.FieldMessage;
import com.truelanz.catalog.dto.UserUpdateDTO;
import com.truelanz.catalog.entities.User;
import com.truelanz.catalog.repositories.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {

    @Autowired
    private UserRepository userRepository;

	@Autowired
	private HttpServletRequest request;
	
	@Override
	public void initialize(UserUpdateValid ann) {
	}

	@Override
	public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {
		
		List<FieldMessage> list = new ArrayList<>();

		// obter os valores - {id} - da variável de caminho (path variables) como Map<String, String>.
		// convertes variável String -> long;
		@SuppressWarnings("unchecked")
		var uriVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		long userId = Long.parseLong(uriVars.get("id"));
		
		// --- testes de validação: ---

        //Verificar se email atualizado já existe no banco de dados
        User userEmail = userRepository.findByEmail(dto.getEmail());
        if (userEmail != null && userId != userEmail.getId()) {
            list.add(new FieldMessage("email", "email already exists"));
        }

		
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getField())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}
