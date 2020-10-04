package ru.karpuninAlek.demo.model.DTOs;

import ru.karpuninAlek.demo.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {
    public String login;
    public String name;
    public String password;
    public List<RoleDTO> roles;

    //region Initializers
    protected UserDTO() {}

    public UserDTO(String login, String name, String password) {
        this.login = login;
        this.name = name;
        this.password = password;
        this.roles = new ArrayList<>();
    }

    public UserDTO(User user) {
        this.login = user.getLogin();
        this.name = user.getName();
        this.password = user.getPassword();
        this.roles = new ArrayList<>();
        user.getRoles().forEach(role -> this.roles.add(new RoleDTO(role.getName())));
    }
    //endregion

    public boolean equals(Object obj) {
        try {
            UserDTO another = (UserDTO) obj;
            if (login.equals(another.login)
                    && name.equals(another.name)
                    && password.equals(another.password)
                    && (
                            (roles == null && another.roles == null)
                                    || (
                                            roles.size() == another.roles.size()
                                                    && roles.containsAll(another.roles)
                                                    && another.roles.containsAll(roles)
                                        )
                        )
                ) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
