package ru.karpuninAlek.demo.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.karpuninAlek.demo.model.Role;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findById(Long id);

    Optional<Role> findByName(String name);

}
