package com.example.meta.store.werehouse.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Message;

public interface MessageRepository extends BaseRepository<Message, Long>{

	@Query("SELECT m FROM Message m WHERE (m.receiver = :receiver AND m.sender = :sender) OR (m.sender = :receiver AND m.receiver = :sender)")
	List<Message> findAllBySenderAndReceiver(String receiver, String sender);

	
}
