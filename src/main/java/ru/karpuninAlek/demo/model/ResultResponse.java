package ru.karpuninAlek.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ResultResponse {

    private static List<String> exceptionToErrors(Exception exception) {
        List<String> errors = new ArrayList<>();
        errors.add(exception.getLocalizedMessage());
        return errors;
    }

    @NonNull
    private boolean success = true;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errors = null;

    //region Initializers
    public ResultResponse(){}

    public ResultResponse(Exception exception) throws IllegalArgumentException {
        this(exceptionToErrors(exception));
    }

    public ResultResponse(List<String> errors) throws IllegalArgumentException {
        this.success = false;
        this.errors = errors;
    }
    //endregion

    public boolean isSuccess() {
        return success;
    }

    public List<String> getErrors() {
        return errors;
    }
}
