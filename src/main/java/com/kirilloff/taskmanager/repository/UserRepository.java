package com.kirilloff.taskmanager.repository;

import com.kirilloff.taskmanager.domain.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, UUID> {
  Optional<User> findByUsername(String username);
}
