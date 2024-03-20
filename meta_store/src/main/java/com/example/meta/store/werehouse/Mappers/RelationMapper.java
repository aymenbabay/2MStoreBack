package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;

import com.example.meta.store.werehouse.Dtos.SubArticleRelationDto;
import com.example.meta.store.werehouse.Entities.SubArticle;

@Mapper
public interface RelationMapper {

	SubArticle mapToEntity(SubArticleRelationDto dto);
	
	SubArticleRelationDto mapToDto(SubArticle entity);
}
