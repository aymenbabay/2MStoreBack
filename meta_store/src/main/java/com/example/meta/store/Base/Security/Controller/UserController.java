package com.example.meta.store.Base.Security.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meta.store.Base.Security.Dto.UserDto;
import com.example.meta.store.Base.Security.Entity.AuthenticationRequest;
import com.example.meta.store.Base.Security.Entity.AuthenticationResponse;
import com.example.meta.store.Base.Security.Entity.RegisterRequest;
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.werehouse.Controllers.ArticleController;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200/")
@RequiredArgsConstructor
@Validated
public class UserController {
	
	private final UserService userService;
	
	private final PasswordEncoder passwordEncoder;

	 private final  ServletContext context;
	 
	 private static final long EXPIRE_TOKEN_AFTER_MINUTES = 30;

	 @GetMapping("")
		public String sayHi() {
			return "hi";
		}

	 
		private final Logger logger = LoggerFactory.getLogger(UserController.class);
	@GetMapping("/all")
	public ResponseEntity<?> getAll(){
		List<User> users = userService.findAll();
		return ResponseEntity.ok(users);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<?> getById(@PathVariable Long id ) {
		return ResponseEntity.ok(userService.findById(id));
	}
	
	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid RegisterRequest request){
		logger.warn("je mrigeul fi registre ");
		return ResponseEntity.ok(userService.register(request));
	}
	
	@PostMapping("/authentication")
	public ResponseEntity<AuthenticationResponse> authentication(@RequestBody AuthenticationRequest request){
		logger.warn("it's ok "+request.getUsername()+" password "+request.getPassword());
		return ResponseEntity.ok(userService.authenticate(request));
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody String token){
		return ResponseEntity.ok(userService.refreshToken(token));
	}
	
//	 @GetMapping("/users/verif/{email}/{pwd}")
//	    public String findUserByEmail(@PathVariable String email, @PathVariable String pwd, HttpServletRequest request) {
//		 System.out.println(email+pwd);
//	    	 Optional<User> user = userService.findUserByEmail(email);
//		 String appUrl = request.getScheme() + "://" + request.getServerName()+":4200";
//		 if (!user.isPresent()) {
//				System.out.println( "We didn't find an account for that e-mail address.");
//				return "0";
//			} else {
//				User userr = user.get();
//				String pss = passwordEncoder.encode(pwd);
//				System.out.println(pss);
//				if (userr.getPassword().equals(pwd))
//				{
//				userr.setDateToken(LocalDateTime.now());
//				userr.setResetToken(UUID.randomUUID().toString());
//  			userService.save(userr);
//  			SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//  			simpleMailMessage.setFrom("testab.symfony@gmail.com");
//  			simpleMailMessage.setTo(userr.getEmail());
//  			simpleMailMessage.setSubject("Password Reset Request");
//  			simpleMailMessage.setText("Pou récupérer votre Mot De passe cliquer sur ce Lien :\n" + appUrl
// 					+ "/resetpwd?token=" + userr.getResetToken());
//  			System.out.println(userr.getResetToken());
// 			userService.sendEmail(simpleMailMessage);
// 			return "1";
//				}
//				
//				else
//				{
//					System.out.println( "Mot de Passe Incorrecte");
//					return ("2");
//			}
//			
//	   }
//	 }
	 
	 @GetMapping("/username/{username}")
	 public boolean checkUserName(@PathVariable String username) {
		 return userService.checkUserName(username);
	 }
	 
	 @GetMapping("/get/{username}")
	 public List<UserDto> getByUserName(@PathVariable String username) {
		 return userService.getByUserName(username);
	 }
}
