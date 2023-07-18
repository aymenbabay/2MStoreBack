package com.example.meta.store.Base.Security.Repository;

import java.util.Optional;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.Base.Security.Entity.User;


public interface UserRepository extends BaseRepository<User, Long> {

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	boolean existsByUsername(String username);
}
