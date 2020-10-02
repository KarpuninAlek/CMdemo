package ru.karpuninAlek.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import ru.karpuninAlek.demo.model.DTOs.UserDTO;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {

    private static final int MAX_LOGIN_LENGTH = 100;
    private static final int MAX_PASSWORD_LENGTH = 200;
    private static final int MAX_NAME_LENGTH = 300;

    @Id
    @NotNull
    private String login;

    @NotNull
    private String name;
    @NotNull
    private String password;

    @Transient
    @JsonIgnore
    private final List<String> errors = new ArrayList<>();

    protected User() {}

    public User(UserDTO dto) {
        this(dto.login, dto.name, dto.password);
    }

    public User(String login, String name, String password) {
        setLogin(login);
        setName(name);
        setPassword(password);
    }

    public String getLogin() {
        return login;
    }

    public static boolean isLoginOfLength(String login) {
        final int length = login.length();
        return length <= MAX_LOGIN_LENGTH && length > 0;
    }

    public void setLogin(String login) {
        if (login == null) {
            errors.add("Login can't be null");
            return;
        }
        if (login.matches(".* +.*")) {
            errors.add("Login has spaces");
        }
        if (!isLoginOfLength(login)) {
            errors.add("Login is too long");
        }
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            errors.add("Name can't be null");
            return;
        }
        if (name.length() > MAX_NAME_LENGTH) {
            errors.add("Name is too long");
        }
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null) {
            errors.add("Password can't be null");
            return;
        }
        if (!(password.matches(".*\\d.*") && password.matches(".*\\p{Lu}.*"))) {
            errors.add("Password is not up to security standard");
        }
        if (name.length() > MAX_PASSWORD_LENGTH) {
            errors.add("Password is too long");
        }
        this.password = password;
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean isFaulty(){
        return errors.size() > 0;
    }

    public User updatedFrom(User another){
        name = another.name;
        password = another.password;
        return this;
    }
}
