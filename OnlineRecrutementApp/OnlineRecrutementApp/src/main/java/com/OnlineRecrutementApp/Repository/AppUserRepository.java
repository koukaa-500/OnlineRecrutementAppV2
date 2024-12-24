package com.OnlineRecrutementApp.Repository;

import com.OnlineRecrutementApp.Entity.AppUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUsers , Long> {
    Optional<AppUsers> findByUsername(String username);
    Optional<AppUsers> findByEmail(String email);
}
