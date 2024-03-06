package com.example.meta.store.werehouse.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Payment;

public interface PaymentRepository extends BaseRepository<Payment, Long> {

	@Query("SELECT p FROM Payment p WHERE p.invoice.company.id = :companyId OR p.invoice.client.id = :clientId")
	List<Payment> findAllByCompanyIdOrClientId(Long companyId, Long clientId);

}
