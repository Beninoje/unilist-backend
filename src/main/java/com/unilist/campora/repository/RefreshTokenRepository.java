package com.unilist.campora.repository;

import com.unilist.campora.model.RefreshToken;
import com.unilist.campora.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface
RefreshTokenRepository extends CrudRepository<RefreshToken,UUID> {
    Optional<RefreshToken> findByToken(UUID token);

    void deleteByUser(User user);

    Optional<RefreshToken> findByUser(User user);
}
