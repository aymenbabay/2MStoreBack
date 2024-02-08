package com.example.meta.store.werehouse.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.PurchaseOrder;

public interface PurchaseOrderRepository extends BaseRepository<PurchaseOrder, Long>{

	List<PurchaseOrder> findAllByCompanyId(Long id);

	@Query("SELECT p FROM PurchaseOrder p WHERE (p.company.id = :companyId) OR (p.client.id = :clientId) OR (p.pclient.id = :pClientId)")
	List<PurchaseOrder> findAllByCompanyIdOrClientIdOrPclientId(Long companyId, Long clientId, Long pClientId);

	@Query("SELECT MAX(p.orderNumber) FROM PurchaseOrder p WHERE (p.client.id = :clientId) OR (p.pclient.id = :pClientId)")
	Long getLastOrderNumber(Long clientId, Long pClientId);
	
}
