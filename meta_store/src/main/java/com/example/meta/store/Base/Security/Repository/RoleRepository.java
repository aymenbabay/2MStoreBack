package com.example.meta.store.Base.Security.Repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.Base.Security.Entity.Role;
import com.example.meta.store.Base.Security.Enums.RoleEnum;


public interface RoleRepository extends BaseRepository<Role, Long> {

	Optional<Role> findByName(RoleEnum name);
	
    @Query(value = "SELECT r.* FROM role r " +
            "INNER JOIN user_roles ur ON r.id = ur.role_id " +
            "INNER JOIN user u ON ur.user_id = u.id " +
            "WHERE u.id = :userId", nativeQuery = true)
    Set<Role> findRolesByUserId(@Param("userId") Long userId);

}
