package ru.yandex.practicum.commerce.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.warehouse.entity.ProductOnWarehouseEntity;
import ru.yandex.practicum.commerce.warehouse.mapper.WarehouseMapper;
import ru.yandex.practicum.commerce.warehouse.repository.ProductOnWarehouseRepository;
import ru.yandex.practicum.commerce.warehouse.util.RandomAddressGenerator;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseService {
    
    private final ProductOnWarehouseRepository productOnWarehouseRepository;
    private final WarehouseMapper warehouseMapper;
    
    @Transactional
    public void addNewProductToWarehouse(NewProductInWarehouseRequest request) {
        ProductOnWarehouseEntity entity = warehouseMapper.toEntity(request);
        entity.setQuantity(0);
        
        productOnWarehouseRepository.save(entity);
    }
    
    @Transactional(readOnly = true)
    public BookedProductsDto checkAvailability(ShoppingCartDto cart) {
        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean isAnyFragile = false;
        
        for (Map.Entry<UUID, Integer> entry : cart.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Integer quantity = entry.getValue();
            
            ProductOnWarehouseEntity product = productOnWarehouseRepository
                    .findByProductId(productId)
                    .orElse(null);
                    
            if (product != null) {
                totalWeight += (product.getWeight() != null ? product.getWeight() : 0.0) * quantity;
                totalVolume += (product.getWidth() != null ? product.getWidth() : 0.0) *
                              (product.getHeight() != null ? product.getHeight() : 0.0) *
                              (product.getDepth() != null ? product.getDepth() : 0.0) * quantity;
                
                if (product.getFragile()) {
                    isAnyFragile = true;
                }
            }
        }
        
        BookedProductsDto result = new BookedProductsDto();
        result.setDeliveryWeight(totalWeight);
        result.setDeliveryVolume(totalVolume);
        result.setFragile(isAnyFragile);
        
        return result;
    }
    
    @Transactional
    public void addProductQuantity(AddProductToWarehouseRequest request) {
        productOnWarehouseRepository.findByProductId(request.getProductId())
                .ifPresentOrElse(
                    product -> {
                        product.setQuantity(product.getQuantity() + request.getQuantity());
                        productOnWarehouseRepository.save(product);
                    },
                    () -> {
                        throw new RuntimeException("Product not found on warehouse");
                    }
                );
    }
    
    @Transactional(readOnly = true)
    public AddressDto getAddress() {
        String addressValue = RandomAddressGenerator.getCurrentAddress();
        
        AddressDto address = new AddressDto();
        address.setCountry(addressValue);
        address.setCity(addressValue);
        address.setStreet(addressValue);
        address.setHouse(addressValue);
        address.setFlat(addressValue);
        
        return address;
    }
}