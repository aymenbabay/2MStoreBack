package com.example.meta.store.Base.Security.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.Base.Security.Entity.User;


public interface UserRepository extends BaseRepository<User, Long> {

	Optional<User> findByUsername(String username);
	
	@Query("SELECT u FROM User u JOIN u.roles r WHERE u.username like %:username% AND r.id = 2")
	List<User> searchByName(String username);

	Optional<User> findByEmail(String email);

	boolean existsByUsername(String username);
}
