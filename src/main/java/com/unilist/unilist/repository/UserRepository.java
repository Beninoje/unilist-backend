package com.unilist.unilist.repository;


import com.unilist.unilist.model.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationCode(String verificationCode);
    Optional<User> findById(Long userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.favourites WHERE u.id = :id")
    Optional<User> findByIdWithFavourites(@Param("id") Long id);
}
