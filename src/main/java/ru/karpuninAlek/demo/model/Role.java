package ru.karpuninAlek.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;

import javax.persistence.*;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    @NotNull
    private String name;
    @JsonIgnore
    @NotNull
    private Long count = 0l;

    protected Role() {}

    public Role(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role incremented(){
        count++;
        return this;
    }

    public Role decremented(){
        count--;
        return this;
    }

    public boolean isInUse(){
        return count > 0;
    }
}
