package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.meta.store.werehouse.Dtos.ArticleUpdateDto;
import com.example.meta.store.werehouse.Dtos.CompanyArticleDto;
import com.example.meta.store.werehouse.Entities.CompanyArticle;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Mapper
public interface CompanyArticleMapper {

	CompanyArticle mapToEntity(CompanyArticleDto dto);
	
	CompanyArticleDto mapToDto(CompanyArticle entity);
	

    @Mapping(source = "article", target = "article.id")
    @Mapping(source = "company", target = "company.id")
    @Mapping(source = "category", target = "category.id")
    @Mapping(source = "subCategory", target = "subCategory.id")
	CompanyArticle mapToCompanyArticle(ArticleUpdateDto update);
}
