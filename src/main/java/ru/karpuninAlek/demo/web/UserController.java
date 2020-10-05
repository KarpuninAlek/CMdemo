package ru.karpuninAlek.demo.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.karpuninAlek.demo.model.DTOs.UserDTO;
import ru.karpuninAlek.demo.model.ResultResponse;
import ru.karpuninAlek.demo.repositories.UserService;

import java.util.*;

@RestController
public class UserController {

    //region Common responses

    private static ResponseEntity<ResultResponse> badRequestResponse(Exception exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResultResponse(exception));
    }

    private static ResponseEntity<ResultResponse> internalErrorResponse(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResultResponse(exception));
    }

    private static ResponseEntity<ResultResponse> notFoundResponse(Exception exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResultResponse(exception));
    }

    private static ResponseEntity<ResultResponse> unprocessableEntity(ResultResponse result) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(result);
    }

    //endregion

    @Autowired
    UserService userService;

    @RequestMapping(value = "users", method = RequestMethod.GET)
    public ResponseEntity<?> getUsers(){
        try {
            return ResponseEntity.ok(userService.getAll());
        } catch (Exception e) {
            return internalErrorResponse(e);
        }
    }

    @RequestMapping(value = "users/{login}", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@PathVariable String login){
        try {
            return ResponseEntity.ok(userService.getBy(login));
        } catch (IllegalArgumentException e) {
            return badRequestResponse(e);
        } catch (NoSuchElementException e) {
            return notFoundResponse(e);
        } catch (Exception e) {
            return internalErrorResponse(e);
        }
    }

    @RequestMapping(value = "users", method = RequestMethod.POST)
    public ResponseEntity<ResultResponse> setUser(@RequestBody UserDTO dto){
        try {
            ResultResponse result = userService.save(dto);
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return unprocessableEntity(result);
            }

        } catch (IllegalArgumentException e) {
            return badRequestResponse(e);
        } catch (Exception e) {
            return internalErrorResponse(e);
        }
    }

    @RequestMapping(value = "users/{login}", method = RequestMethod.DELETE)
    public ResponseEntity<ResultResponse> deleteUser(@PathVariable String login){
        try {
            userService.delete(login);
            return ResponseEntity.ok(new ResultResponse());
        } catch (IllegalArgumentException e) {
            return badRequestResponse(e);
        } catch (NoSuchElementException e) {
            return notFoundResponse(e);
        } catch (Exception e) {
            return internalErrorResponse(e);
        }
    }

    @RequestMapping(value = "users/{login}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@PathVariable String login, @RequestBody UserDTO dto){
        try {
            ResultResponse result = userService.update(login, dto);
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return unprocessableEntity(result);
            }
        } catch (IllegalArgumentException e) {
            return badRequestResponse(e);
        } catch (NoSuchElementException e) {
            return notFoundResponse(e);
        } catch (Exception e) {
            return internalErrorResponse(e);
        }
    }
}
