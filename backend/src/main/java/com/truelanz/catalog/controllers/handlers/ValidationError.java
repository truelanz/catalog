package com.truelanz.catalog.controllers.handlers;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class ValidationError extends StandardError {

    private List<FieldMessage> fieldErrors = new ArrayList<>();
    
    public void addError(String fieldName, String message) {
        fieldErrors.add(new FieldMessage(fieldName, message));
    }
}
