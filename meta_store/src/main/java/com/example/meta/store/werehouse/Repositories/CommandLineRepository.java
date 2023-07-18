package com.example.meta.store.werehouse.Repositories;

import java.util.List;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.CommandLine;

public interface CommandLineRepository extends BaseRepository<CommandLine, Long> {

	List<CommandLine> findByInvoiceCode(Long code);

	List<CommandLine> findByInvoiceId( Long invoiceId);
	
	void deleteByInvoiceId(Long invoiceId);

}
