package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Cash;

public interface CashRepository extends BaseRepository<Cash, Long>{


	List<Cash> findAllByInvoiceId(Long id);

}
