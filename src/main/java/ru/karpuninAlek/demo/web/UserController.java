package ru.karpuninAlek.demo.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.karpuninAlek.demo.model.Role;
import ru.karpuninAlek.demo.model.User;
import ru.karpuninAlek.demo.repositories.RoleRepository;
import ru.karpuninAlek.demo.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "users", method = RequestMethod.GET)
    public @ResponseBody
    List<User> getUsers(){
        return userRepository.findAllBy();
    }

    @RequestMapping(value = "users", method = RequestMethod.POST)
    public @ResponseBody
    User setUser(@RequestBody User user){
        try {
            final User savedUser = userRepository.save(user);
            return savedUser;
        } catch (Exception e) {
            return null;
        }
    }

    @RequestMapping(value = "users/{login}", method = RequestMethod.GET)
    public @ResponseBody
    User getUser(@PathVariable String login){
        try {
            User found = userRepository.findByLogin(login);
            return found;
        } catch (Exception e) {
            return null;
        }
    }

    @RequestMapping(value = "users/{login}", method = RequestMethod.DELETE)
    public @ResponseBody
    User deleteUser(@PathVariable String login){
        try {
            User found = userRepository.findByLogin(login);
            if (found != null) {
                userRepository.delete(found);
                return found;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @RequestMapping(value = "users/{login}", method = RequestMethod.PUT)
    public @ResponseBody
    User updateUser(@PathVariable String login, @RequestBody User user){
        try {
            User found = userRepository.findByLogin(login);
            if (found != null) {
                userRepository.delete(found);
                userRepository.save(user);
                return found;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
