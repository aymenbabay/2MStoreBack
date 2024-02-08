package com.example.meta.store.werehouse.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.PurchaseOrderLine;

public interface PurchaseOrderLineRepository extends BaseRepository<PurchaseOrderLine, Long>{

	@Query("SELECT p FROM PurchaseOrderLine p JOIN PurchaseOrder po WHERE p.id = :id AND (po.client.id = :clientId OR po.pclient.id = :pClientId)")
	Optional<PurchaseOrderLine> findByIdAndClientIdOrPassingClientId(Long id, Long clientId, Long pClientId);



}
