package com.example.meta.store.werehouse.Repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.SubArticle;

public interface SubArticleRepository  extends BaseRepository<SubArticle, Long> {

	
}
