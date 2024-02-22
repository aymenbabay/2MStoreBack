package com.example.meta.store.werehouse.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.meta.store.Base.ErrorHandler.RecordNotFoundException;
import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Dtos.OrderDeliveryDto;
import com.example.meta.store.werehouse.Entities.OrderDelivery;
import com.example.meta.store.werehouse.Mappers.OrderDeliveryMapper;
import com.example.meta.store.werehouse.Repositories.OrderDeliveryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderDeliveryService extends BaseService<OrderDelivery, Long> {

	private final OrderDeliveryRepository orderDeliveryRepository;
	
	private final OrderDeliveryMapper orderDeliveryMapper;
	
	public List<OrderDeliveryDto> getAllOrderForAymen(){
		List<OrderDelivery> orderDeliveries = orderDeliveryRepository.findAll();
		if(orderDeliveries.isEmpty()) {
			throw new RecordNotFoundException("there is no order yet");
		}
		List<OrderDeliveryDto> orderDeliveriesDto = new ArrayList<>();
		for(OrderDelivery i : orderDeliveries) {
			OrderDeliveryDto orderDeliveryDto = orderDeliveryMapper.mapToDto(i);
			orderDeliveriesDto.add(orderDeliveryDto);
		}
		return orderDeliveriesDto;
	}
	
	
	
}
