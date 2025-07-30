package org.example.server.repositories;

import org.example.server.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @Query("SELECT COUNT(u) > 0 FROM UserEntity u WHERE u.auth0_id = :auth")
    boolean existsByAuth0_id(@Param("auth") String auth);

    @Query("SELECT u FROM UserEntity u WHERE u.auth0_id = :auth")
    Optional<UserEntity> findByAuth0_id(String auth);
}
