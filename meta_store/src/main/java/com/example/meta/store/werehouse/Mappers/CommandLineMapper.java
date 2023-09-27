package com.example.meta.store.werehouse.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.meta.store.werehouse.Dtos.CommandLineDto;
import com.example.meta.store.werehouse.Entities.CommandLine;

@Mapper
public interface CommandLineMapper {


 //   @Mapping(source = "article", target = "article.id")
//    @Mapping(source = "articleTva", target = "article.tva")
//    @Mapping(source = "articleCost", target = "article.cost")
//    @Mapping(source = "articleLibelle", target = "article.libelle")
//    @Mapping(source = "articleMargin", target = "article.margin")
  //  @Mapping(source = "articleCode", target = "article.code")
//    @Mapping(source = "articleUnit", target = "article.unit")
	CommandLine mapToEntity(CommandLineDto dto);
	


 //   @Mapping(source = "article.id", target = "article")
//    @Mapping(source = "article.tva", target = "articleTva")
//    @Mapping(source = "article.cost", target = "articleCost")
//    @Mapping(source = "article.libelle", target = "articleLibelle")
//    @Mapping(source = "article.margin", target = "articleMargin")
  //  @Mapping(source = "article.code", target = "articleCode")
//    @Mapping(source = "article.unit", target = "articleUnit")
	CommandLineDto mapToDto(CommandLine entity);
}
