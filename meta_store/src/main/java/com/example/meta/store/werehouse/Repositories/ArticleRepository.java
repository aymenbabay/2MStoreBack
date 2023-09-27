package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Article;
import com.example.meta.store.werehouse.Enums.PrivacySetting;

public interface ArticleRepository extends BaseRepository<Article, Long>{

	@Query("SELECT a FROM Article a WHERE a.provider.id = :providerId AND a.libelle = :libelle")
	List<Article> findAllByLibelleAndProviderIdContaining(String libelle, Long providerId);

	
	@Query(value = "SELECT a FROM Article a WHERE "
			+ " (a.isVisible = :publi"
			+ " AND ABS(a.provider.company.user.logitude - :longitude) <5000 "
			+ " AND ABS(a.provider.company.user.latitude - :latitude) <5000)"
			+ " ORDER BY random() LIMIT 10 "
			)
    List<Article> findRandomArticles(double longitude, double latitude, PrivacySetting publi );

	@Query(value = "SELECT a FROM Article a WHERE"
			+ " (a.provider.id = :providerId) "
			+ " OR (((a.isVisible = :publi)"
			+ " OR (a.isVisible = :cli AND (a.provider.id IN (SELECT p.id FROM Client c JOIN c.providers p WHERE c.id = :clientId))))"
			+ " AND ABS(a.provider.company.user.logitude - :longitude) <5000 "
			+ " AND ABS(a.provider.company.user.latitude - :latitude) <5000) "
			+ "ORDER BY random() LIMIT 10 ")
    List<Article> findRandomArticlesPro(double longitude, double latitude,Long providerId,Long clientId,PrivacySetting publi, PrivacySetting cli);

	List<Article> findAllByCompanyId(Long companyId);


	Optional<Article> findBySharedPointAndProviderId(String sharedPoint, Long id);



	@Query(value = "SELECT a FROM Article a WHERE"
			+ " (a.isVisible = :ps)")
	List<Article> test(PrivacySetting ps);
}
 