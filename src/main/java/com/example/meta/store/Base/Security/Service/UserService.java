package com.example.meta.store.Base.Security.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.ErrorHandler.RecordIsAlreadyExist;
import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Security.Config.JwtAuthenticationFilter;
import com.example.meta.store.Base.Security.Config.JwtService;
import com.example.meta.store.Base.Security.Dto.UserDto;
import com.example.meta.store.Base.Security.Entity.AuthenticationRequest;
import com.example.meta.store.Base.Security.Entity.AuthenticationResponse;
import com.example.meta.store.Base.Security.Entity.RegisterRequest;
import com.example.meta.store.Base.Security.Entity.Role;
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.Base.Security.Mappers.UserMapper;
import com.example.meta.store.Base.Security.Repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	private static final long EXPIRE_TOKEN_AFTER_MINUTES = 60;
	
	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final JwtService jwtService;
	
	private final AuthenticationManager authenticationManager;
	
	private final RoleService roleService;
	
	private final UserMapper userMapper;

	private final JwtAuthenticationFilter authenticationFilter;

	
	public List<User> findAll(){
		return userRepository.findAll();
	}
	
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}
	

	@CacheEvict(value = "user", key = "#root.methodName", allEntries = true)
	public ResponseEntity<User> insert(User user) {
		return ResponseEntity.ok(userRepository.save(user));
	}

	//@Cacheable(value = "user", key = "#root.methodName + '_'+ #name")
	public User findByUserName(String name) {
		
		Optional<User> user = userRepository.findByUsername(name);
		if(user.isEmpty()) {
			throw new RecordNotFoundException("there is no User with name "+name);
		}
		return user.get();
	}
	
	

	public AuthenticationResponse register(RegisterRequest request) {
		Set<Role> role = new HashSet<>();
		ResponseEntity<Role> role1 = roleService.getById((long)2);
		role.add(role1.getBody());
		Optional<User> userr = userRepository.findByUsername(request.getUsername());
		if(userr.isPresent()) {
			throw new RecordIsAlreadyExist("This User Name Is Already Uses Please Take Another One");
		}
		User user = User.builder()
				.phone(request.getPhone())
				.username(request.getUsername())
				.address(request.getAddress())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.roles( role)
				.build();
		userRepository.save(user);
		var jwtToken = jwtService.generateToken(user);
		
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		var user = userRepository.findByUsername(request.getUsername()).orElseThrow();
var jwtToken = jwtService.generateToken(user);
		
		return AuthenticationResponse.builder().token(jwtToken).build();
	
	}
	
	

	public Optional<User> findUserByEmail(String email) {
		
		return userRepository.findByEmail(email);
	}

	public void save(User userr) {
		userRepository.save(userr);
		
	}

	public boolean checkUserName(String username) {
		// TODO Auto-generated method stub
		return userRepository.existsByUsername(username);
	}

	public UserDto getByUserName(String username) {
		Optional<User> user = userRepository.findByUsername(username);
		UserDto appUserDto = userMapper.mapToDto(user.get());
		return appUserDto;
	}

	public AuthenticationResponse refreshToken(String token) {
		User user = findByUserName(authenticationFilter.userName);
		 // validate the input token
	    if (!jwtService.isTokenValid(token,user)) {
	       // throw new InvalidTokenException("Invalid refresh token");
	    }
	    
	    
	    // generate a new authentication token
	    String accessToken = jwtService.generateToken(user);
	    
	    // create a new authentication response
	    AuthenticationResponse response = new AuthenticationResponse(accessToken);
	    
	    // return the response
	    return response;
	}
}
