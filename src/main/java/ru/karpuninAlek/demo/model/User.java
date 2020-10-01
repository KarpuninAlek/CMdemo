package ru.karpuninAlek.demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    private String login;

    private String name;
    private String password;

    protected User() {}

    public User(String login) {
        this.login = login;
    }

    public User(String login, String name, String password) throws IllegalArgumentException {
        this.login = login;
        this.name = name;
        setPassword(password);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws IllegalArgumentException {
        if (!(password.matches(".*\\d.*") && password.matches(".*\\p{Lu}.*"))) {
            throw new IllegalArgumentException("Password is not up to security standard");
        }
        this.password = password;
    }
}
