package ru.karpuninAlek.demo.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.karpuninAlek.demo.repositories.RoleRepository;
import ru.karpuninAlek.demo.model.Role;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RoleController {

    @Autowired
    RoleRepository roleRepository;

    @RequestMapping(value = "roles", method = RequestMethod.GET)
    public @ResponseBody
    List<Role> getRoles(){
        return new ArrayList<Role>();
    }

    @RequestMapping(value = "roles", method = RequestMethod.POST)
    public @ResponseBody
    Role setRole(@RequestBody String roleName){
        try {
            final Role role = roleRepository.save(new Role(roleName));
            return role;
        } catch (Exception e) {
            return null;
        }
    }

}
