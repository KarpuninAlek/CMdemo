package ru.karpuninAlek.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import ru.karpuninAlek.demo.model.DTOs.UserDTO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {

    private static final int MAX_LOGIN_LENGTH = 100;
    private static final int MAX_PASSWORD_LENGTH = 200;
    private static final int MAX_NAME_LENGTH = 300;

    public static final String NULL_LOGIN = "Login can't be null";
    public static final String NULL_NAME = "Name can't be null";
    public static final String NULL_PASSWORD = "Password can't be null";
    public static final String EMPTY_LOGIN = "Login can't be empty";
    public static final String EMPTY_NAME = "Name can't be empty";
    public static final String EMPTY_PASSWORD = "Password can't be empty";
    public static final String SPACE_LOGIN = "Login must not contain space symbols";
    public static final String WEAK_PASSWORD = "Password is not up to security standard";
    public static final String LONG_LOGIN = "Login is too long";
    public static final String LONG_NAME = "Name is too long";
    public static final String LONG_PASSWORD = "Password is too long";


    //region Fields

    @Id
    @NotNull
    private String login;

    @NotNull
    private String name;
    @NotNull
    private String password;

    @ManyToMany(mappedBy = "users")
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();

    @Transient
    @JsonIgnore
    private final List<String> errors = new ArrayList<>();

    //endregion

    //region Initializers
    protected User() {}

    public User(UserDTO dto) {
        this(dto.login, dto.name, dto.password);
    }

    public User(String login, String name, String password) {
        setLogin(login);
        setName(name);
        setPassword(password);
    }
    //endregion

    //region Getters

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getErrors() {
        return errors;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    //endregion

    //region Setters

    public void setLogin(String login) {
        if (login == null) {
            errors.add(NULL_LOGIN);
            return;
        }
        if (login.isEmpty()) {
            errors.add(EMPTY_LOGIN);
            return;
        }
        if (login.matches(".* +.*")) {
            errors.add(SPACE_LOGIN);
        }
        if (login.length() > MAX_LOGIN_LENGTH) {
            errors.add(LONG_LOGIN);
        }
        this.login = login;
    }

    public void setName(String name) {
        if (name == null) {
            errors.add(NULL_NAME);
            return;
        }
        if (name.isEmpty()) {
            errors.add(EMPTY_NAME);
            return;
        }
        if (name.length() > MAX_NAME_LENGTH) {
            errors.add(LONG_NAME);
        }
        this.name = name;
    }

    public void setPassword(String password) {
        if (password == null) {
            errors.add(NULL_PASSWORD);
            return;
        }
        if (password.isEmpty()) {
            errors.add(EMPTY_PASSWORD);
            return;
        }
        // ".*\\d.*" = contains a number
        // ".*\\p{Lu}.*" = contains a capital letter
        if (!(password.matches(".*\\d.*") && password.matches(".*\\p{Lu}.*"))) {
            errors.add(WEAK_PASSWORD);
        }
        if (password.length() > MAX_PASSWORD_LENGTH) {
            errors.add(LONG_PASSWORD);
        }
        this.password = password;
    }

    public void setRoles(Set<Role> roles) {
        if (roles != null) {
            this.roles = roles;
        }
    }

    //endregion

    public void updateFrom(User another){
        if (another != null) {
            this.name = another.name;
            this.password = another.password;
        }
    }

    public void addError(String error) {
        errors.add(error);
    }

    public static boolean isLoginOfLength(String login) {
        final int length = login.length();
        return length <= MAX_LOGIN_LENGTH && length > 0;
    }

    @JsonIgnore
    public boolean isFaulty(){
        return errors.size() > 0;
    }




}
