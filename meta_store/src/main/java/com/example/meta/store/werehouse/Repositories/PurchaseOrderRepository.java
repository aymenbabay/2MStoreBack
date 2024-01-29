package com.example.meta.store.werehouse.Repositories;

import java.util.List;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.PurchaseOrder;
import com.example.meta.store.werehouse.Entities.PurchaseOrderLine;

public interface PurchaseOrderRepository extends BaseRepository<PurchaseOrder, Long>{

	List<PurchaseOrder> findAllByCompanyId(Long id);

	
}
