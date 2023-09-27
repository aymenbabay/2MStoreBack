package com.example.meta.store.werehouse.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.Base.Security.Entity.User;
import com.example.meta.store.werehouse.Entities.Conversation;

public interface ConversationRepository extends BaseRepository<Conversation, Long>{

	@Query("SELECT c FROM Conversation c WHERE (c.user1 = :user1 AND c.user2 = :user2) Or (c.user1 = :user2 AND c.user2 = :user1)")
	Conversation findAllByUser1AndUser2(User user1, User user2);

	@Query("SELECT c FROM Conversation c WHERE c.user1 = :me OR c.user2 = :me")
	List<Conversation> findAllByUser1OrUser2(User me);

}
