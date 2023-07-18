package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Article;
import com.example.meta.store.werehouse.Entities.CompanyArticle;

public interface CompanyArticleRepository extends BaseRepository<CompanyArticle, Long> {

	List<CompanyArticle> findByCompanyId(Long id);


	Optional<CompanyArticle> findByArticleIdAndCompanyId(Long articleId, Long companyId);

}
