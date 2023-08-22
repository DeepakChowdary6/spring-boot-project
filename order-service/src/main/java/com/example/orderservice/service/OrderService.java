package com.example.orderservice.service;

import com.example.orderservice.config.WebClientConfig;
import com.example.orderservice.dto.InventoryResponse;
import com.example.orderservice.dto.OrderLineItemsDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.event.OrderPlacedEvent;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItems;
import com.example.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.orderservice.dto.InventoryResponse;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;



@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    public final OrderRepository orderRepository;

     private final WebClient.Builder webClientBuilder;

     private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;



    public String placeOrder(OrderRequest orderRequest){
        Order order=new Order();
      order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList().stream().map(orderLineItemsDto -> maptoDto(orderLineItemsDto)).toList();

     order.setOrderLineItemsList(orderLineItems);

     List<String> skuCodes=order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();
     //call inventory service and place order if product is in
        InventoryResponse[] inventoryResponseArray=webClientBuilder.build().get().uri("http://inventoryservice/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                        .retrieve().bodyToMono(InventoryResponse[].class).block()
                        ;
        boolean allProductsInStock=Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);
        if(allProductsInStock){
            orderRepository.save(order);
            kafkaTemplate.send("notificationtopic",new OrderPlacedEvent(order.getOrderNumber()));
            return "Ordered placed Successfully";
        }else {
            throw new IllegalArgumentException("Product is not in stock please try again");
        }

    }

    private OrderLineItems maptoDto(OrderLineItemsDto orderLineItemsDto){
        OrderLineItems orderLineItems=new OrderLineItems();

        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

}
