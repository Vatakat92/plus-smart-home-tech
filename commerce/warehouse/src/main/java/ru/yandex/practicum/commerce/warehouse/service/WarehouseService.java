package ru.yandex.practicum.commerce.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.warehouse.entity.OrderBooking;
import ru.yandex.practicum.commerce.warehouse.entity.ProductOnWarehouseEntity;
import ru.yandex.practicum.commerce.warehouse.exception.ProductNotFoundOnWarehouseException;
import ru.yandex.practicum.commerce.warehouse.mapper.WarehouseMapper;
import ru.yandex.practicum.commerce.warehouse.repository.OrderBookingRepository;
import ru.yandex.practicum.commerce.warehouse.repository.ProductOnWarehouseRepository;
import ru.yandex.practicum.commerce.warehouse.validation.WarehouseValidationService;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WarehouseService {

    private final ProductOnWarehouseRepository productRepo;
    private final OrderBookingRepository orderBookingRepository;
    private final WarehouseMapper warehouseMapper;
    private final WarehouseValidationService validationService;

    @Transactional
    public void addNewProductToWarehouse(NewProductInWarehouseRequest request) {
        validationService.validateNewProduct(request);
        ProductOnWarehouseEntity entity = warehouseMapper.toEntity(request);
        entity.setQuantity(0);
        productRepo.save(entity);
    }

    public BookedProductsDto checkAvailability(ShoppingCartDto cart) {
        validationService.validateCart(cart);

        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean isAnyFragile = false;

        for (Map.Entry<UUID, Long> entry : cart.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            ProductOnWarehouseEntity product = productRepo.findByProductId(productId)
                    .orElse(null);

            if (product != null) {
                totalWeight += (product.getWeight() != null ? product.getWeight() : 0.0) * quantity;
                totalVolume += (product.getWidth() != null ? product.getWidth() : 0.0) *
                        (product.getHeight() != null ? product.getHeight() : 0.0) *
                        (product.getDepth() != null ? product.getDepth() : 0.0) * quantity;

                if (product.getFragile()) isAnyFragile = true;
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
        validationService.validateAddQuantity(request);

        ProductOnWarehouseEntity product = productRepo.findByProductId(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundOnWarehouseException("Product not found"));

        product.setQuantity(product.getQuantity() + request.getQuantity());
        productRepo.save(product);
    }

    public AddressDto getAddress() {
        AddressDto address = new AddressDto();
        address.setCountry("Random country");
        address.setCity("Random city");
        address.setStreet("ADDRESS_1");
        address.setHouse("1");
        address.setFlat("1");
        return address;
    }

    @Transactional
    public BookedProductsDto assemblyProductForOrderFromShoppingCart(AssemblyProductsForOrderRequest request) {
        log.info("Assembling products for order: {}", request.getOrderId());

        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean isAnyFragile = false;

        // Check availability and reduce quantities
        for (Map.Entry<UUID, Long> entry : request.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            ProductOnWarehouseEntity product = productRepo.findByProductId(productId)
                    .orElseThrow(() -> new ProductNotFoundOnWarehouseException("Product not found: " + productId));

            if (product.getQuantity() < quantity) {
                throw new ProductNotFoundOnWarehouseException("Not enough quantity for product: " + productId);
            }

            // Reduce available quantity
            product.setQuantity(product.getQuantity() - quantity.intValue());
            productRepo.save(product);

            // Calculate totals
            totalWeight += (product.getWeight() != null ? product.getWeight() : 0.0) * quantity;
            totalVolume += (product.getWidth() != null ? product.getWidth() : 0.0) *
                    (product.getHeight() != null ? product.getHeight() : 0.0) *
                    (product.getDepth() != null ? product.getDepth() : 0.0) * quantity;

            if (product.getFragile()) isAnyFragile = true;
        }

        // Create booking record
        OrderBooking booking = OrderBooking.builder()
                .orderId(request.getOrderId())
                .products(request.getProducts())
                .build();
        orderBookingRepository.save(booking);

        BookedProductsDto result = new BookedProductsDto();
        result.setDeliveryWeight(totalWeight);
        result.setDeliveryVolume(totalVolume);
        result.setFragile(isAnyFragile);
        return result;
    }

    @Transactional
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        log.info("Marking order as shipped to delivery: {}", request.getOrderId());

        OrderBooking booking = orderBookingRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Booking not found for order: " + request.getOrderId()));

        booking.setDeliveryId(request.getDeliveryId());
        orderBookingRepository.save(booking);
    }

    @Transactional
    public void acceptReturn(Map<UUID, Long> products) {
        log.info("Accepting return of products: {}", products.keySet());

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            ProductOnWarehouseEntity product = productRepo.findByProductId(productId)
                    .orElseThrow(() -> new ProductNotFoundOnWarehouseException("Product not found: " + productId));

            product.setQuantity(product.getQuantity() + quantity.intValue());
            productRepo.save(product);
        }
    }
}
