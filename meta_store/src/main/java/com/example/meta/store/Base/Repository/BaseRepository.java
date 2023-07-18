package com.example.meta.store.Base.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.example.meta.store.Base.Entity.BaseDto;


@NoRepositoryBean
public interface BaseRepository<T,ID> extends JpaRepository<T, ID> {

	BaseDto<Long> findCompanyById(Long id);
}
