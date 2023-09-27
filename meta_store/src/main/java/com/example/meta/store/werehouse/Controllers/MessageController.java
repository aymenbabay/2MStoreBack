package com.example.meta.store.werehouse.Controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meta.store.Base.Security.Config.JwtAuthenticationFilter;
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.Base.Security.Service.UserService;
import com.example.meta.store.werehouse.Dtos.ConversationDto;
import com.example.meta.store.werehouse.Dtos.MessageDto;
import com.example.meta.store.werehouse.Services.MessageService;

import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("/werehouse/message/")
@RequiredArgsConstructor
public class MessageController {

	private final MessageService messageService;
	
	private final JwtAuthenticationFilter authenticationFilter;

	private final UserService userService;

	private final Logger logger = LoggerFactory.getLogger(MessageController.class);
	
	@PostMapping("{receive}")
	public void sendMessage(@RequestBody MessageDto message, @PathVariable String receive) {
		logger.warn(""+message.getContent());
		User user = getUser();
		User receiver = userService.findByUserName(receive);
		messageService.sendMessage(message, receiver,user);
	} 
	
	@GetMapping("get_message/{user}")
	public List<MessageDto> getAllMyMessage(@PathVariable String user){
		String me = authenticationFilter.userName;
		return messageService.getAllMyMessage(me,user);
	}
	
	@GetMapping("get_conversation")
	public List<ConversationDto> getAllConversation(){
		logger.warn("haw je lil conversation");
		User user = getUser();
		return messageService.getAllMyConversation(user);
	}
	
	private User getUser() {

		User user = userService.findByUserName(authenticationFilter.userName);
		return user;
	}
}
