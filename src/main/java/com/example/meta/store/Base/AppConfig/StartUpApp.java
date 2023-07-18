package com.example.meta.store.Base.AppConfig;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.meta.store.Base.Security.Entity.Role;
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.Base.Security.Service.RoleService;
import com.example.meta.store.Base.Security.Service.UserService;


@Component
public class StartUpApp implements CommandLineRunner {

	@Autowired
	private RoleService roleService;
	
	@Autowired
	private UserService appUserService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public void run(String... args) throws Exception {

		if(roleService.findAll().isEmpty()) {
		insertRole("admin");
		insertRole("user");
		Set<Role> adminRole = new HashSet<>();
		adminRole.add(roleService.findByName("admin"));
		adminRole.add(roleService.findByName("user"));
		insertUser(adminRole);
		
		}
	}
	

	public ResponseEntity<?> insertRole(String rol){
		Role role = new Role(rol);
		return roleService.insert(role);
	}
	
	public ResponseEntity<?> insertUser(Set<Role> roles){
		
		User user = new User("aymen babay","user","aymen1",passwordEncoder.encode("password"),roles);
		
		return appUserService.insert(user);
	}
}
