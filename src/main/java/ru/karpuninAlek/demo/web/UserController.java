package ru.karpuninAlek.demo.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.karpuninAlek.demo.model.DTOs.UserDTO;
import ru.karpuninAlek.demo.model.ResultResponse;
import ru.karpuninAlek.demo.model.User;
import ru.karpuninAlek.demo.repositories.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class UserController {

    private static final ResponseEntity<ResultResponse> illegalLogin = ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ResultResponse(new IllegalArgumentException("Passed login isn't a possible one")));

    private static ResponseEntity<ResultResponse> internalErrorResponse(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResultResponse(exception));
    }

    private static ResponseEntity<ResultResponse> internalErrorResponse(Exception exception, List<String> errors) {
        errors.add(exception.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResultResponse(errors));
    }

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "users", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<?> getUsers(){
        try {
            return ResponseEntity.ok(userRepository.findAllBy());
        } catch (Exception e) {
            return internalErrorResponse(e);
        }

    }

    @RequestMapping(value = "users", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<?> setUser(@RequestBody UserDTO dto){
        User user = new User(dto);
        if (user.isFaulty()) {
            return ResponseEntity.badRequest().body(new ResultResponse(user.getErrors()));
        }
        try {
            userRepository.save(user);
            return ResponseEntity.ok(new ResultResponse());
        }
        catch (Exception e) {
            return internalErrorResponse(e);
        }
    }

    @RequestMapping(value = "users/{login}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<?> getUser(@PathVariable String login){
        if (!User.isLoginOfLength(login)) {
            return illegalLogin;
        }
        try {
            User found = userRepository.findByLogin(login);
            return ResponseEntity.ok(found);
        } catch (Exception e) {
            return internalErrorResponse(e);
        }
    }

    @RequestMapping(value = "users/{login}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<?> deleteUser(@PathVariable String login){
        if (!User.isLoginOfLength(login)) {
            return illegalLogin;
        }
        try {
            if (userRepository.existsByLogin(login)) {
                userRepository.deleteById(login);
                return ResponseEntity.ok(new ResultResponse());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResultResponse(new NoSuchElementException("No user with such login found")));
            }
        } catch (Exception e) {
            return internalErrorResponse(e);
        }
    }

    @RequestMapping(value = "users/{login}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<?> updateUser(@PathVariable String login, @RequestBody UserDTO dto){
        if (!User.isLoginOfLength(login)) {
            return illegalLogin;
        }
        User user = new User(dto);
        List<String> errors = user.getErrors();
        try {
            if (!userRepository.existsByLogin(login)) {
                errors.add("User with given login doesn't exist");
            }
            if (!login.equals(user.getLogin()) && !userRepository.existsByLogin(user.getLogin())) {
                errors.add("Can't change user's login to already existing one");
            }
            if (errors.size() > 0) {
                return ResponseEntity.badRequest().body(new ResultResponse(errors));
            }
            if (login.equals(user.getLogin())) {
                User existing = userRepository.findByLogin(login);
                userRepository.save(existing.updatedFrom(user));
            } else {
                userRepository.deleteById(login);
                userRepository.save(user);
            }
            return ResponseEntity.ok(new ResultResponse());

        } catch (Exception e) {
            return internalErrorResponse(e, errors);
        }
    }
}
