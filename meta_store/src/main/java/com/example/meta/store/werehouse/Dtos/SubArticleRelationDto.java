package com.example.meta.store.werehouse.Dtos;

import java.io.Serializable;

import com.example.meta.store.Base.Entity.BaseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubArticleRelationDto extends BaseDto<Long> implements Serializable {

	
	private SubArticleDto parentArticle;
	
	private SubArticleDto childArticle;
	
	private Double quantity;
}
