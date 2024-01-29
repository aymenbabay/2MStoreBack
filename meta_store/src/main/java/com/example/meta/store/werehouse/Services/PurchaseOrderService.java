package com.example.meta.store.werehouse.Services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Controllers.PurchaseOrderController;
import com.example.meta.store.werehouse.Dtos.PurchaseOrderDto;
import com.example.meta.store.werehouse.Dtos.PurchaseOrderLineDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.PassingClient;
import com.example.meta.store.werehouse.Entities.PurchaseOrder;
import com.example.meta.store.werehouse.Entities.PurchaseOrderLine;
import com.example.meta.store.werehouse.Enums.Status;
import com.example.meta.store.werehouse.Mappers.PurchaseOrderLineMapper;
import com.example.meta.store.werehouse.Mappers.PurchaseOrderMapper;
import com.example.meta.store.werehouse.Repositories.PurchaseOrderLineRepository;
import com.example.meta.store.werehouse.Repositories.PurchaseOrderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PurchaseOrderService extends BaseService<PurchaseOrderLine, Long> {

	private final PurchaseOrderLineRepository purchaseOrderLineRepository;
	
	private final PurchaseOrderRepository purchaseOrderRepository;

	private final PurchaseOrderLineMapper purchaseOrderLineMapper;

	private final PurchaseOrderMapper purchaseOrderMapper;

	private final Logger logger = LoggerFactory.getLogger(PurchaseOrderService.class);

	public void addPurchaseOrder(List<PurchaseOrderLineDto> purchaseOrderDto, Client client, PassingClient pClient) {
		logger.warn(purchaseOrderDto.size()+ " size");
		Company company = null;
		Set<PurchaseOrderLine> purchaseOrderLines = new HashSet<>();
		for(PurchaseOrderLineDto i : purchaseOrderDto) {
			PurchaseOrderLine purchaseOrderLine = purchaseOrderLineMapper.mapToEntity(i);
			logger.warn(i.getQuantity()+" quantity ");
			if(company == null || company != purchaseOrderLine.getArticle().getCompany()) {	
				PurchaseOrder purchaseOrder = new PurchaseOrder();
				if(client == null) {
					purchaseOrder.setPclient(pClient);
				}else {
					purchaseOrder.setClient(client);
				}
				purchaseOrderLines = new HashSet<>();
				purchaseOrder.setLines(purchaseOrderLines);
				purchaseOrder.setStatus(Status.INWAITING);
				purchaseOrder.setOrderNumber("O01");
				purchaseOrder.setCompany(purchaseOrderLine.getArticle().getCompany());
				purchaseOrderRepository.save(purchaseOrder);
			}
			 company = purchaseOrderLine.getArticle().getCompany();
			 purchaseOrderLines.add(purchaseOrderLine);
			purchaseOrderLineRepository.save(purchaseOrderLine);
		}

	}

	public List<PurchaseOrderDto> getAllMyPurchaseOrder(Company company) {
		List<PurchaseOrder> purchaseOrder = purchaseOrderRepository.findAllByCompanyId(company.getId());
		if(purchaseOrder.isEmpty()) {
			throw new RecordNotFoundException("there is no order");
		}
		List<PurchaseOrderDto> purchaseOrdersDto = new ArrayList<>();
		for(PurchaseOrder i : purchaseOrder) {
			PurchaseOrderDto purchaseOrderDto = purchaseOrderMapper.mapToDto(i);
			purchaseOrdersDto.add(purchaseOrderDto);
		}
		return purchaseOrdersDto;
	}
	
	
}
