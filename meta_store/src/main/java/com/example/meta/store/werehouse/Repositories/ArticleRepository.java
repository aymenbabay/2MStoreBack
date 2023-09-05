package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Article;

public interface ArticleRepository extends BaseRepository<Article, Long>{

	@Query("SELECT a FROM Article a WHERE a.provider.id = :providerId AND a.libelle = :libelle")
	List<Article> findAllByLibelleAndProviderIdContaining(String libelle, Long providerId);

	
	@Query(value = "SELECT a FROM Article a WHERE ABS(a.provider.company.user.logitude - :longitude) >5 "
			+ "AND ABS(a.provider.company.user.latitude - :latitude) >5 ORDER BY random() LIMIT 10 ")
    List<Article> findRandomArticles(double longitude, double latitude );

	List<Article> findAllByProviderId(Long providerId);




}
