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

    public Role(String name) {
        this.name = name;
    }

    public Role(RoleDTO dto) {
        this.name = dto.name;
    }

    //endregion

    //region Getters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<User> getUsers() {
        return users;
    }

    //endregion

    //region Setters

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
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
