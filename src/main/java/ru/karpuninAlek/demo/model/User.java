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
            errors.add("Login can't be null");
            return;
        }
        if (login.matches(".* +.*")) {
            errors.add("Login must not contain space symbols");
        }
        if (!isLoginOfLength(login)) {
            errors.add("Login is too long");
        }
        this.login = login;
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

    public void setPassword(String password) {
        if (password == null) {
            errors.add("Password can't be null");
            return;
        }
        // ".*\\d.*" = contains a number
        // ".*\\p{Lu}.*" = contains a capital letter
        if (!(password.matches(".*\\d.*") && password.matches(".*\\p{Lu}.*"))) {
            errors.add("Password is not up to security standard");
        }
        if (name.length() > MAX_PASSWORD_LENGTH) {
            errors.add("Password is too long");
        }
        this.password = password;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    //endregion

    public User updatedFrom(User another){
        this.name = another.name;
        this.password = another.password;
        this.roles = another.getRoles();
        return this;
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
