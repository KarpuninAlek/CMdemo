package ru.karpuninAlek.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import ru.karpuninAlek.demo.model.DTOs.RoleDTO;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role")
public class Role {

    private static final int MAX_NAME_LENGTH = 100;

    public static final String NULL_NAME = "Role's name can't be null";
    public static final String EMPTY_NAME = "Role's name can't be empty";
    public static final String LONG_NAME = "Role's name is too long";

    //region Fields

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @JoinColumn(name = "user_login", nullable = false)
    private Long id;

    @NotNull
    private String name;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
    })
    @JoinTable(
            name = "role_user",
            joinColumns = {@JoinColumn(name = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_login")}
    )
    @JsonIgnore
    private final Set<User> users = new HashSet<>();

    //endregion

    //region Initializers

    protected Role() {}

    public Role(String name) throws IllegalArgumentException {
        setName(name);
    }

    public Role(RoleDTO dto) throws IllegalArgumentException {
        this(dto.name);
    }

    //endregion

    //region Getters

    public String getName() {
        return name;
    }

    public Set<User> getUsers() {
        return users;
    }

    //endregion

    //region Setters

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException(NULL_NAME);
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_NAME);
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(LONG_NAME);
        }
        this.name = name;
    }

    //endregion

    public void addUser(User user) {
        this.users.add(user);
    }

    public void removeUser(User user) {
        this.users.remove(user);
    }
}
