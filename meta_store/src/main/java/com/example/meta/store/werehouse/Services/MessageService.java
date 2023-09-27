package com.example.meta.store.werehouse.Services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Controllers.ArticleController;
import com.example.meta.store.werehouse.Dtos.ConversationDto;
import com.example.meta.store.werehouse.Dtos.MessageDto;
import com.example.meta.store.werehouse.Entities.Conversation;
import com.example.meta.store.werehouse.Entities.Message;
import com.example.meta.store.werehouse.Mappers.ConversationMapper;
import com.example.meta.store.werehouse.Mappers.MessageMapper;
import com.example.meta.store.werehouse.Repositories.ConversationRepository;
import com.example.meta.store.werehouse.Repositories.MessageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class MessageService extends BaseService<Message, Long> {
	
	private final MessageRepository messageRepository;
	
	private final ConversationRepository conversationRepository;
	
	private final MessageMapper messageMapper;
	
	private final ConversationMapper conversationMapper;

	private final Logger logger = LoggerFactory.getLogger(MessageService.class);
	
	public void sendMessage(MessageDto message, User receiver, User me) {
		Message mess = messageMapper.mapToEntity(message);
		mess.setReceiver(receiver.getUsername());
		mess.setSender(me.getUsername());
		Conversation conversation = conversationRepository.findAllByUser1AndUser2(receiver,me);
		Set<Message> messages = new HashSet<>();
		if(conversation == null) {	
			conversation  = new Conversation();
			conversation.setUser1(me);
			conversation.setUser2(receiver);
		}else {
			messages.addAll(conversation.getMessage());
		}
		messages.add(mess);
		conversation.setMessage(messages);			
		messageRepository.save(mess);
		conversationRepository.save(conversation);
		
	}

	public List<MessageDto> getAllMyMessage(String me, String user) {
		List<Message> messages = messageRepository.findAllBySenderAndReceiver(me,user);
		List<MessageDto> messagesDto = new ArrayList<>();
		for(Message i : messages) {
			MessageDto dto = messageMapper.mapToDto(i);
			messagesDto.add(dto);
		logger.warn(dto.getContent()+" get all message");
		}
		return messagesDto;
	}
	
	public List<ConversationDto> getAllMyConversation(User me){
		logger.warn("haw je lil conversation");
		List<Conversation> conversations = conversationRepository.findAllByUser1OrUser2(me);
		List<ConversationDto> conversationsDto = new ArrayList<>();
		for(Conversation i : conversations) {
			ConversationDto conver = conversationMapper.mapToDto(i);
			conversationsDto.add(conver);
			logger.warn(i.getId()+" conversation id");
		}
		return conversationsDto;
	}

}
