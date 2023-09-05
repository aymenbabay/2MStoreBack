package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.meta.store.werehouse.Dtos.CommandLineDto;
import com.example.meta.store.werehouse.Entities.CommandLine;

@Mapper
public interface CommandLineMapper {


    @Mapping(source = "companyArticle", target = "companyArticle.id")
    @Mapping(source = "articleTva", target = "companyArticle.article.tva")
    @Mapping(source = "articleCost", target = "companyArticle.cost")
    @Mapping(source = "articleLibelle", target = "companyArticle.article.libelle")
    @Mapping(source = "articleMargin", target = "companyArticle.margin")
    @Mapping(source = "articleCode", target = "companyArticle.article.code")
    @Mapping(source = "articleUnit", target = "companyArticle.article.unit")
	CommandLine mapToEntity(CommandLineDto dto);
	

   @Mapping(source = "companyArticle.id", target = "companyArticle")
   @Mapping(source = "companyArticle.article.tva", target = "articleTva")
   @Mapping(source = "companyArticle.cost", target = "articleCost")
   @Mapping(source = "companyArticle.article.libelle", target = "articleLibelle")
   @Mapping(source = "companyArticle.margin", target = "articleMargin")
   @Mapping(source = "companyArticle.article.code", target = "articleCode")
   @Mapping(source = "companyArticle.article.unit", target = "articleUnit")
	CommandLineDto mapToDto(CommandLine entity);
}
