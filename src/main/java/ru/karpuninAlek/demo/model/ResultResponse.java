package ru.karpuninAlek.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ResultResponse {

    private static final int MAX_ERROR_LENGTH = 100;

    @NonNull
    private boolean success = true;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errors = null;

    public ResultResponse(){}

    private static List<String> exceptionToErrors(Exception exception) {
        List<String> errors = new ArrayList<>();
        errors.add(exception.getLocalizedMessage());
        return errors;
    }

    public ResultResponse(Exception exception) throws IllegalArgumentException {
        this(exceptionToErrors(exception));
    }

    public ResultResponse(List<String> errors) throws IllegalArgumentException {
        if (errors.stream().anyMatch(error -> error.length() > MAX_ERROR_LENGTH)) {
            throw new IllegalArgumentException("Error description is too long");
        }
        this.success = false;
        this.errors = errors;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getErrors() {
        return errors;
    }
}
