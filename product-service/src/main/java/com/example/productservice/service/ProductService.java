package com.example.productservice.service;

import com.example.productservice.dto.ProductRequest;
import com.example.productservice.dto.ProductResponse;
import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Builder
@Data
@Service
public class ProductService {


    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void createProduct(ProductRequest productRequest){
        Product product= Product.builder().name(productRequest.getName()).description(productRequest.getDescription()).price(productRequest.getPrice())
                .build();

        productRepository.save(product);
        log.info("product is "+product.getId()+" saved");
    }

    
    public List<ProductResponse> getAllProducts() {
        List<Product>products = productRepository.findAll();
        return products.stream().map(product -> mapToProductResponse(product)).toList();
    }
    private ProductResponse mapToProductResponse(Product product){

        return ProductResponse.builder().id(product.getId()).name(product.getName()).description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
