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
import com.example.meta.store.Base.Security.Enums.RoleEnum;
import com.example.meta.store.Base.Security.Service.RoleService;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.werehouse.Entities.Delivery;
import com.example.meta.store.werehouse.Services.DeliveryService;


@Component
public class StartUpApp implements CommandLineRunner {

	@Autowired
	private RoleService roleService;
	
	@Autowired
	private UserService appUserService;
	
	@Autowired
	private DeliveryService deliveryService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public void run(String... args) throws Exception {

		if(roleService.findAll().isEmpty()) {
		insertRole(RoleEnum.ADMIN);
		insertRole(RoleEnum.USER);
		insertRole(RoleEnum.WORKER);
		Set<Role> adminRole = new HashSet<>();
		adminRole.add(roleService.findByName(RoleEnum.ADMIN));
		adminRole.add(roleService.findByName(RoleEnum.USER));
		insertUser(adminRole);
		insertDelivery();
		}
	}
	

	public ResponseEntity<?> insertRole(RoleEnum rol){
		Role role = new Role();
		role.setName(rol);
		return roleService.insert(role);
	}
	
	public ResponseEntity<?> insertUser(Set<Role> roles){
		
		User user = new User("97396321","user","aymen1@gmail.com",passwordEncoder.encode("password"),roles);
		
		return appUserService.insert(user);
	}
	
	public void insertDelivery() {
		User user = appUserService.findById((long)1).get();
		Delivery aymen = new Delivery(user, null, null);
		deliveryService.insert(aymen);
		
	}
}
