package com.example.meta.store.werehouse.Controllers;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.meta.store.werehouse.Dtos.OrderDeliveryDto;
import com.example.meta.store.werehouse.Services.OrderDeliveryService;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/werehouse/order_delivery/")
@RequiredArgsConstructor
public class OrderDeliveryController {

	private final OrderDeliveryService orderDeliveryService;
	
	@GetMapping()
	public List<OrderDeliveryDto> getAllOrderForAymen(){
		return orderDeliveryService.getAllOrderForAymen();
	}
	
}
