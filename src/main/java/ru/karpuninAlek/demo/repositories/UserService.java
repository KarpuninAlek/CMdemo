package ru.karpuninAlek.demo.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.karpuninAlek.demo.model.DTOs.RoleDTO;
import ru.karpuninAlek.demo.model.DTOs.UserDTO;
import ru.karpuninAlek.demo.model.ResultResponse;
import ru.karpuninAlek.demo.model.Role;
import ru.karpuninAlek.demo.model.User;

import java.util.*;

@Service
public class UserService {

    public static final String NOT_POSSIBLE_LOGIN = "Passed login isn't a possible one";
    public static final String USER_EXISTS = "User with such login already exists";
    public static final String NULL_DTO = "No user was passed";

    private static IllegalArgumentException illegalLogin() {
        return new IllegalArgumentException(NOT_POSSIBLE_LOGIN);
    }

    private static IllegalArgumentException nullDto() {
        return new IllegalArgumentException(NULL_DTO);
    }

    private static NoSuchElementException noUserFound() {
        return new NoSuchElementException("User with such login doesn't exist");
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    public boolean exists(String login) {
        return User.isLoginOfLength(login) && userRepository.existsByLogin(login);
    }

    boolean exists(User user) {
        return exists(user.getLogin());
    }

    public List<User> getAll() throws Exception{
        return userRepository.findAllBy();
    }

    void loginCheck(String login) throws IllegalArgumentException {
        if (login == null || !User.isLoginOfLength(login)) {
            throw illegalLogin();
        }
    }

    void saveRoleWithUser(Role role, User user) {
        Optional<Role> existingRole = roleRepository.findByName(role.getName());
        existingRole.ifPresentOrElse(surelyExistingRole -> surelyExistingRole.addUser(user),
                () -> {
                    role.addUser(user);
                    roleRepository.save(role);
                });
    }

    @Transactional
    public ResultResponse save(UserDTO dto) throws Exception {
        if (dto == null) {
            throw nullDto();
        }
        User user = new User(dto);

        if (user.isFaulty()) {
            return new ResultResponse(user.getErrors());
        }
        if (exists(user)) {
            throw new IllegalArgumentException(USER_EXISTS);
        }

        if (dto.roles != null && !dto.roles.isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (RoleDTO roleDto: dto.roles) {
                roles.add(new Role(roleDto));
            }

            roles.forEach(role -> saveRoleWithUser(role, user));

            user.setRoles(new HashSet<>(roles));
        }

        userRepository.save(user);

        return new ResultResponse();
    }

    public UserDTO getBy(String login) throws Exception {
        loginCheck(login);
        User found = userRepository.findByLogin(login);
        if (found == null) {
            throw noUserFound();
        }
        return new UserDTO(found);
    }

    void deleteUserFromRole(User user, Role role) {
        role.removeUser(user);
        if (role.getUsers().isEmpty()) {
            roleRepository.delete(role);
        }
    }

    @Transactional
    public void delete(String login) throws Exception {
        loginCheck(login);
        if (!exists(login)){
            throw noUserFound();
        }

        User user = userRepository.findByLogin(login);
        user.getRoles().forEach(role -> deleteUserFromRole(user, role));
        userRepository.delete(user);
    }

    User checkedForUpdateUser(String login, UserDTO dto) throws Exception{
        if (dto == null) {
            throw nullDto();
        }
        if (!exists(login)) {
            throw noUserFound();
        }
        User user = new User(dto);
        if (!login.equals(user.getLogin()) && userRepository.existsByLogin(user.getLogin())) {
            user.addError("Can't change user's login to already existing one");
        }
        if (dto.roles != null) {
            Set<Role> roles = new HashSet<>();
            for (RoleDTO roleDTO: dto.roles) {
                if (roleRepository.existsByName(roleDTO.name)) {
                    roleRepository.findByName(roleDTO.name).ifPresent(roles::add);
                } else {
                    roles.add(roleRepository.save(new Role(roleDTO.name)));
                }
            }
            user.setRoles(roles);
        }

        return user;
    }

    @Transactional
    public ResultResponse update(String login, UserDTO dto) throws Exception {
        loginCheck(login);
        User user = checkedForUpdateUser(login, dto);
        if (user.isFaulty()) {
            return new ResultResponse(user.getErrors());
        }

        if (login.equals(user.getLogin())) {
            User existing = userRepository.findByLogin(login);

            if (dto.roles != null) {
                Set<Role> rolesToAdd = new HashSet<>(user.getRoles());
                rolesToAdd.removeAll(existing.getRoles());
                rolesToAdd.forEach(role -> saveRoleWithUser(role, existing));

                Set<Role> rolesToFree = new HashSet<>(existing.getRoles());
                rolesToFree.removeAll(user.getRoles());
                rolesToFree.forEach(role -> deleteUserFromRole(existing, role));


                existing.setRoles(user.getRoles());
            }
            existing.updateFrom(user);
        } else {
            delete(login);
            userRepository.save(user);
        }

        return new ResultResponse();
    }

}
