package com.example.meta.store.werehouse.Services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.meta.store.Base.ErrorHandler.RecordIsAlreadyExist;
import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Controllers.PurchaseOrderController;
import com.example.meta.store.werehouse.Dtos.PurchaseOrderDto;
import com.example.meta.store.werehouse.Dtos.PurchaseOrderLineDto;
import com.example.meta.store.werehouse.Entities.Client;
import com.example.meta.store.werehouse.Entities.Company;
import com.example.meta.store.werehouse.Entities.Delivery;
import com.example.meta.store.werehouse.Entities.OrderDelivery;
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
public class PurchaseOrderService extends BaseService<PurchaseOrder, Long> {

	private final PurchaseOrderLineRepository purchaseOrderLineRepository;
	
	private final PurchaseOrderRepository purchaseOrderRepository;

	private final PurchaseOrderLineMapper purchaseOrderLineMapper;

	private final PurchaseOrderMapper purchaseOrderMapper;
	
	private final DeliveryService deliveryService;

	private final OrderDeliveryService orderDeliveryService;

	private final Logger logger = LoggerFactory.getLogger(PurchaseOrderService.class);

	
	public void addPurchaseOrder(List<PurchaseOrderLineDto> purchaseOrderDto, Client client, PassingClient pClient) {
	    Company company = new Company();
	    PurchaseOrder purchaseOrder = null;
	    Long id = null;
      //Sort purchaseOrderDto based on company ID
    Collections.sort(purchaseOrderDto, Comparator.comparing(dto -> dto.getArticle().getCompany().getId()));
	    for (PurchaseOrderLineDto i : purchaseOrderDto) {
	        PurchaseOrderLine purchaseOrderLine = purchaseOrderLineMapper.mapToEntity(i);
	        // Check if a purchase order already exists for the current company
	        if (company.getId() != null && company.getId() == purchaseOrderLine.getArticle().getCompany().getId()) {
	            // Fetch existing purchase order for the company
	            purchaseOrder = purchaseOrderRepository.findById(id).get();
	        } else {
	        	Long orderNumber = (long) 001;
	            // If company is null or different, create a new purchase order
	            purchaseOrder = new PurchaseOrder();
	            if (client.getId() == null) {
	            	logger.warn("client is null");
	                purchaseOrder.setPclient(pClient);
	            } else {
	            	logger.warn("client is not null");
	                purchaseOrder.setClient(client);
	            }
	            Long orderN = purchaseOrderRepository.getLastOrderNumber(client.getId(),pClient.getId());
	            if(orderN != null) {
	            	orderNumber = orderN+1;
	            }
	            purchaseOrder.setOrderNumber(orderNumber);
	            purchaseOrder.setCompany(purchaseOrderLine.getArticle().getCompany());
	           // a supp purchaseOrder.setLines(new HashSet<>());
	            company = purchaseOrderLine.getArticle().getCompany();
	        }
	        purchaseOrderLine.setPurchaseorder(purchaseOrder);
	        // Add the purchaseOrderLine to the purchaseOrder
	       // a supp purchaseOrder.getLines().add(purchaseOrderLine);
	        purchaseOrderLine.setStatus(Status.INWAITING);
	        purchaseOrderLineRepository.save(purchaseOrderLine);
	        purchaseOrderRepository.save(purchaseOrder);
	        id = purchaseOrder.getId();
	    }
	}



	public List<PurchaseOrderDto> getAllMyPurchaseOrder(Client client, PassingClient pClient) {
		List<PurchaseOrder> purchaseOrderLine;
		if(client.getCompany() == null) {	
			if(pClient.getId() == null) {
				throw new RecordNotFoundException("there is no Order");
			}
			 purchaseOrderLine = purchaseOrderRepository.findAllByCompanyIdOrClientIdOrPclientId(null,client.getId(), pClient.getId());
		}else {			
			purchaseOrderLine = purchaseOrderRepository.findAllByCompanyIdOrClientIdOrPclientId(client.getCompany().getId(),client.getId(), pClient.getId());
		}
		if(purchaseOrderLine.isEmpty()) {
			throw new RecordNotFoundException("there is no order");
		}
		List<PurchaseOrderDto> purchaseOrdersDto = new ArrayList<>();
		for(PurchaseOrder i : purchaseOrderLine) {
			PurchaseOrderDto purchaseOrderLineDto = purchaseOrderMapper.mapToDto(i);
			purchaseOrdersDto.add(purchaseOrderLineDto);
		}
		return purchaseOrdersDto;
	}

	
	public PurchaseOrderDto getOrderById(Long id, Optional<Client> client, Optional<PassingClient> pClient) {
	    PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
	            .orElseThrow(() -> new RecordNotFoundException("There is no order with id: " + id));

	    if (pClient.isPresent() && pClient.get().equals(purchaseOrder.getPclient())) {
	        return purchaseOrderMapper.mapToDto(purchaseOrder);
	    }

	    if (client.isPresent() && (client.get().equals(purchaseOrder.getClient()) || client.get().getCompany().equals(purchaseOrder.getCompany()))) {
	        return purchaseOrderMapper.mapToDto(purchaseOrder);
	    }

	    throw new RecordNotFoundException("There is no order with id: " + id);
	}



	public void OrderResponse(Long id, Status status, Company company) {
		logger.warn("status 1 "+status);
	    PurchaseOrderLine purchaseOrderLine = purchaseOrderLineRepository.findById(id)
	            .orElseThrow(() -> new RecordNotFoundException("There is no order with id: " + id));
	    logger.warn("status 2 "+status);
	    purchaseOrderLine.setStatus(status);
	    logger.warn("status 3 "+status);
	    if(status == Status.ACCEPTED && purchaseOrderLine.getDelivery()) {
	    	Delivery deliver = deliveryService.getById((long)1).getBody();
	    	OrderDelivery orderDelivery = new OrderDelivery();
	    	orderDelivery.setOrder(purchaseOrderLine);
	    	orderDelivery.setDelivery(deliver);
	    	orderDeliveryService.insert(orderDelivery);
	    }
		
	}

	public void cancelOrder(Client client, PassingClient pClient, Long id) {
	    PurchaseOrderLine purchaseOrderLine = purchaseOrderLineRepository.findByIdAndClientIdOrPassingClientId(id,client.getId(),pClient.getId())
	    		.orElseThrow(() -> new RecordNotFoundException("there is no order with id: "+id));
		
		
		purchaseOrderLine.setStatus(Status.CANCELLED);
	}



	public void UpdatePurchaseOrderLine(PurchaseOrderLineDto purchaseOrderLineDto, Client client,
			PassingClient passingClient) {
		PurchaseOrderLine purchaseOrderLine = purchaseOrderLineRepository.findByIdAndClientIdOrPassingClientId(purchaseOrderLineDto.getId(), client.getId(), passingClient.getId())
				.orElseThrow(() -> new RecordNotFoundException("there is no order with id: "+purchaseOrderLineDto.getId()));
		if(purchaseOrderLine.getStatus() == Status.INWAITING) {	
			purchaseOrderLine = purchaseOrderLineMapper.mapToEntity(purchaseOrderLineDto);
			logger.warn("status is : "+purchaseOrderLine.getStatus());
		purchaseOrderLineRepository.save(purchaseOrderLine);
		}else {
			throw new RecordIsAlreadyExist("you can not do that because the order is already "+purchaseOrderLine.getStatus());
		}
	}



	public List<PurchaseOrderLineDto> getAllPurchaseOrderLinesByPurchaseOrderId(Long id) {
		List<PurchaseOrderLine> purchaseOrderLines = purchaseOrderLineRepository.findAllByPurchaseorderId(id);
		if(purchaseOrderLines.isEmpty()) {
			throw new RecordNotFoundException("there is no order");
		}
		List<PurchaseOrderLineDto> purchaseOrderLinesDto = new ArrayList<>();
		for(PurchaseOrderLine i : purchaseOrderLines) {
			PurchaseOrderLineDto purchaseOrderLineDto = purchaseOrderLineMapper.mapToDto(i);
			purchaseOrderLinesDto.add(purchaseOrderLineDto);
		}
		return purchaseOrderLinesDto;
	}



	public List<PurchaseOrderLineDto> getAllMyPurchaseOrderLinesByCompanyId(Long companyId, Long clientId, Long pClientId) {
		List<PurchaseOrderLine> purchaseOrderLines = purchaseOrderLineRepository.findAllByCompanyIdOrClientIdOrPclientId(companyId, clientId, pClientId);
		if(purchaseOrderLines.isEmpty()) {
			throw new RecordNotFoundException("there is no order yet");
		}
		List<PurchaseOrderLineDto> purchaseOrderLinesDto = new ArrayList<>();
		for(PurchaseOrderLine i : purchaseOrderLines) {
			PurchaseOrderLineDto purchaseOrderLineDto = purchaseOrderLineMapper.mapToDto(i);
			purchaseOrderLinesDto.add(purchaseOrderLineDto);
		}
		return purchaseOrderLinesDto;
	}
	
}
