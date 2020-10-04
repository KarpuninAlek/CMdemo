package ru.karpuninAlek.demo.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.karpuninAlek.demo.model.DTOs.UserDTO;
import ru.karpuninAlek.demo.model.ResultResponse;
import ru.karpuninAlek.demo.model.Role;
import ru.karpuninAlek.demo.model.User;

import java.util.*;

@Service
public class UserService {

    private static final IllegalArgumentException illegalLogin = new IllegalArgumentException("Passed login isn't a possible one");

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    public boolean exists(String login) {
        if (User.isLoginOfLength(login)) {
            return userRepository.existsByLogin(login);
        } else {
            return false;
        }
    }

    boolean exists(User user) {
        return exists(user.getLogin());
    }

    public List<User> getAll() throws Exception{
        return userRepository.findAllBy();
    }

    void loginCheck(String login) throws IllegalArgumentException {
        if (!User.isLoginOfLength(login)) {
            throw illegalLogin;
        }
    }

    void saveRoleWithUser(Role role, User user) {
        if (roleRepository.existsByName(role.getName())) {
            Optional<Role> existing = roleRepository.findByName(role.getName());
            existing.ifPresent(surelyExisting -> {
                surelyExisting.addUser(user);
            });
        } else {
            role.addUser(user);
            roleRepository.save(role);
        }
    }

    @Transactional
    public ResultResponse save(UserDTO dto) throws Exception {
        User user = new User(dto);

        if (user.isFaulty()) {
            return new ResultResponse(user.getErrors());
        }
        if (exists(user)) {
            throw new IllegalArgumentException("User with such login already exists");
        }

        if (dto.roles.size() > 0) {
            Set<Role> roles = new HashSet<>();
            dto.roles.forEach(role -> roles.add(new Role(role)));

            roles.forEach(role -> saveRoleWithUser(role, user));

            user.setRoles(new HashSet<>(roles));
        }

        userRepository.save(user);

        return new ResultResponse();
    }

    public UserDTO getBy(String login) throws Exception {
        loginCheck(login);
        User found = userRepository.findByLogin(login);
        return new UserDTO(found);
    }

    void deleteUserFromRole(User user, Role role) {
        role.removeUser(user);
        if (role.getUsers().size() == 0) {
            roleRepository.delete(role);
        }
    }

    @Transactional
    public void delete(String login) throws Exception {
        loginCheck(login);
        if (!exists(login)){
            throw new NoSuchElementException("User with such login doesn't exist");
        }

        User user = userRepository.findByLogin(login);
        user.getRoles().forEach(role -> {
            deleteUserFromRole(user, role);
//                role.removeUser(user);
//                if (role.getUsers().size() == 0) {
//                    roleRepository.delete(role);
//                }
        });
        userRepository.delete(user);
    }

    User checkedForUpdateUser(String login, UserDTO dto){
        User user = new User(dto);
        if (!login.equals(user.getLogin()) && !userRepository.existsByLogin(user.getLogin())) {
            user.addError("Can't change user's login to already existing one");
        }
        Set<Role> roles = new HashSet<>();
        dto.roles.forEach(roleDTO -> {
            if (roleRepository.existsByName(roleDTO.name)) {
                roleRepository.findByName(roleDTO.name).ifPresent(roles::add);
            } else {
                roles.add(roleRepository.save(new Role(roleDTO.name)));
            }
        });
        user.setRoles(roles);
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

            Set<Role> rolesToAdd = new HashSet<>(user.getRoles());
            rolesToAdd.removeAll(existing.getRoles());
            rolesToAdd.forEach(role -> saveRoleWithUser(role, existing));

            Set<Role> rolesToFree = new HashSet<>(existing.getRoles());
            rolesToFree.removeAll(user.getRoles());
            rolesToFree.forEach(role -> deleteUserFromRole(existing, role));

            existing.updatedFrom(user);
        } else {
            delete(login);
            userRepository.save(user);
        }

        return new ResultResponse();
    }

}
