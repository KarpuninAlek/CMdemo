package ru.karpuninAlek.demo.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.karpuninAlek.demo.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    // TODO remove
    List<Role> findAllBy();

}
