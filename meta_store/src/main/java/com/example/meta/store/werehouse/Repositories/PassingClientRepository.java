package com.example.meta.store.werehouse.Repositories;

import java.util.Optional;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.PassingClient;

public interface PassingClientRepository extends BaseRepository<PassingClient, Long> {

	Optional<PassingClient> findByUserId(Long id);

}
