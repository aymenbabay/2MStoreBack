package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Article;

public interface ArticleRepository extends BaseRepository<Article, Long>{

	@Query("SELECT a FROM Article a WHERE a.provider.id = :providerId AND a.libelle = :libelle")
	List<Article> findAllByLibelleAndProviderIdContaining(String libelle, Long providerId);

	Optional<Article> findByCode(String art);

	Optional<Article> findByLibelleAndProviderId(String libelle,Long providerId);
//

	List<Article> findByProviderId( Long providerId);

	Optional<Article> findByIdAndProviderId(Long id, Long providerId);

	Optional<Article> findByCodeAndProviderId(String code, Long companyId);


	@Query(value = "SELECT a FROM Article a ORDER BY random() LIMIT 10")
    List<Article> findRandomArticles();

	 boolean existsByCodeAndProviderId(String code, Long providerId);
	
	List<Article> findAllByProviderId(Long providerId);


}
