package ru.karpuninAlek.demo.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.karpuninAlek.demo.model.User;

import java.util.List;

public interface UserRepository extends CrudRepository<User, String> {

    List<User> findAllBy();

    User findByLogin(String login);

    boolean existsByLogin(String login);

    User getByLogin(String login);

    void deleteByLogin(String login);

}
