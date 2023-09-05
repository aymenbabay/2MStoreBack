package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.ClientInvoice;
import com.example.meta.store.werehouse.Enums.InvoiceStatus;

public interface ClientInvoiceRepository extends BaseRepository<ClientInvoice, Long>{

	List<ClientInvoice> findAllByIsAccepted(InvoiceStatus b);

	@Query("SELECT I FROM ClientInvoice I WHERE I.invoice.code = :invoiceCode AND I.client.id = :clientId")
	Optional<ClientInvoice> findByInvoiceCode(Long invoiceCode, Long clientId);

	List<ClientInvoice> findAllByClientIdOrProviderId(Long clientId, Long providerId);

}
