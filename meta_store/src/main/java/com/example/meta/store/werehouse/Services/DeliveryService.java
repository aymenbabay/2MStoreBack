package com.example.meta.store.werehouse.Services;

import org.springframework.stereotype.Service;

import com.example.meta.store.Base.Service.BaseService;
import com.example.meta.store.werehouse.Entities.Delivery;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryService extends BaseService<Delivery, Long> {

}
