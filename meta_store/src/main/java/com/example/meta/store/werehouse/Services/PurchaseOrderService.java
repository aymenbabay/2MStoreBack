package com.example.meta.store.werehouse.Services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Controllers.PurchaseOrderController;
import com.example.meta.store.werehouse.Dtos.PurchaseOrderDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.PassingClient;
import com.example.meta.store.werehouse.Entities.PurchaseOrder;
import com.example.meta.store.werehouse.Mappers.PurchaseOrderMapper;
import com.example.meta.store.werehouse.Repositories.PurchaseOrderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PurchaseOrderService extends BaseService<PurchaseOrder, Long> {

	private final PurchaseOrderRepository purchaseOrderRepository;
	
	private final PurchaseOrderMapper purchaseOrderMapper;

	private final Logger logger = LoggerFactory.getLogger(PurchaseOrderService.class);

	public void addPurchaseOrder(List<PurchaseOrderDto> purchaseOrderDto, Client client, PassingClient pClient) {
		logger.warn(purchaseOrderDto.size()+ " size");
		for(PurchaseOrderDto i : purchaseOrderDto) {
			PurchaseOrder purchaseOrder = purchaseOrderMapper.mapToEntity(i);
			logger.warn(i.getQuantity()+" quantity ");
			if(client == null) {
				purchaseOrder.setPclient(pClient);
			}else {
				purchaseOrder.setClient(client);
			}
			purchaseOrderRepository.save(purchaseOrder);
		}
	}
	
	
}
