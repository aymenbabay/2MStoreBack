package com.example.meta.store.werehouse.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.BankTransfer;

public interface BankTransferRepository extends BaseRepository<BankTransfer, Long> {

	@Query("SELECT b FROM BankTransfer b WHERE b.invoice.id = :id")
	Optional<BankTransfer> findByInvoiceId(Long id);

}
