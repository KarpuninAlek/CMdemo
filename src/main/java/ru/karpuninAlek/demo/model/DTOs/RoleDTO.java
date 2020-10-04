package ru.karpuninAlek.demo.model.DTOs;

public class RoleDTO {
    public String name;

    public RoleDTO(String name) {
        this.name = name;
    }

    protected RoleDTO() {}

    public boolean equals(Object obj) {
        try {
            RoleDTO another = (RoleDTO) obj;
            return name.equals(another.name);
        } catch (Exception e) {
            return false;
        }
    }
}
