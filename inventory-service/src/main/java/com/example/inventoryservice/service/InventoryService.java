package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import  org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import java.util.List;

@Service
@RequiredArgsConstructor
@EnableTransactionManagement
@Slf4j
public class InventoryService {

    public final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode){
//
//        log.info("Wait Started");
//        Thread.sleep(10000);
//        log.info("Wait ended");

       return  inventoryRepository.findBySkuCodeIn(skuCode).stream().
               map(inventory -> InventoryResponse.builder()
                       .skuCode(inventory.getSkuCode())
                       .isInStock(inventory.getQuantity()>0)
                       .build()).toList();

    }
}
