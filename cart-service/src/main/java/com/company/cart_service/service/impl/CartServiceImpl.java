package com.company.cart_service.service.impl;
import com.company.cart_service.client.ProductClient;
import com.company.cart_service.dto.request.AddToCartRequest;
import com.company.cart_service.dto.request.UpdateCartRequest;
import com.company.cart_service.dto.response.CartResponse;
import com.company.cart_service.dto.response.ProductResponse;
import com.company.cart_service.exception.CartNotFoundException;
import com.company.cart_service.exception.InsufficientStockException;
import com.company.cart_service.exception.ProductNotFoundException;
import com.company.cart_service.mapper.CartMapper;
import com.company.cart_service.model.Cart;
import com.company.cart_service.model.CartItem;
import com.company.cart_service.repository.CartItemRepository;
import com.company.cart_service.repository.CartRepository;
import com.company.cart_service.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;

    // ================= ADD ITEM =================
    @Override
    public Mono<CartResponse> addToCart(AddToCartRequest request) {

        log.info("START addToCart | userId={} productId={} quantity={}",
                request.getUserId(), request.getProductId(), request.getQuantity());

        return productClient.getProduct(request.getProductId())
                .switchIfEmpty(Mono.error(new ProductNotFoundException()))
                .doOnNext(p -> log.debug("Product fetched: {}", p))

                .flatMap(product -> validateAndReduceStock(product, request.getQuantity()))

                .flatMap(product ->
                        cartRepository.findByUserId(request.getUserId())
                                .switchIfEmpty(createCart(request.getUserId()))
                                .flatMap(cart -> upsertCartItem(cart, product, request.getQuantity()))
                )

                .flatMap(this::buildCartResponse)

                .doOnSuccess(res -> log.info("END addToCart SUCCESS | userId={}", request.getUserId()))
                .doOnError(err -> log.error("END addToCart FAILED | {}", err.getMessage()));
    }

    // ================= UPDATE CART =================
    @Override
    public Mono<CartResponse> updateCart(UpdateCartRequest request) {

        log.info("START updateCart | userId={} productId={} quantity={}",
                request.getUserId(), request.getProductId(), request.getQuantity());

        return cartRepository.findByUserId(request.getUserId())
                .switchIfEmpty(Mono.error(new CartNotFoundException()))

                .flatMap(cart ->
                        cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                                .switchIfEmpty(Mono.error(new ProductNotFoundException()))

                                .flatMap(item -> adjustStock(item, request.getQuantity()))

                                .then(cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId()))
                                .flatMap(item -> updateOrRemoveItem(cart, item, request.getQuantity()))
                )

                .flatMap(this::buildCartResponse)

                .doOnSuccess(res -> log.info("END updateCart SUCCESS | userId={}", request.getUserId()))
                .doOnError(err -> log.error("END updateCart FAILED | {}", err.getMessage()));
    }

    // ================= REMOVE ITEM =================
    @Override
    public Mono<Void> removeItem(String userId, Long productId) {

        log.info("START removeItem | userId={} productId={}", userId, productId);

        return cartRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new CartNotFoundException()))

                .flatMap(cart ->
                        cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                                .switchIfEmpty(Mono.error(new ProductNotFoundException()))

                                .flatMap(item -> {
                                    log.info("Restoring stock | productId={} quantity={}",
                                            productId, item.getQuantity());

                                    return productClient.increaseStock(productId, item.getQuantity())
                                            .then(cartItemRepository.deleteByCartIdAndProductId(cart.getId(), productId));
                                })
                )

                .doOnSuccess(v -> log.info("END removeItem SUCCESS | userId={}", userId))
                .doOnError(err -> log.error("END removeItem FAILED | {}", err.getMessage()));
    }

    // ================= VIEW CART =================
    @Override
    public Mono<CartResponse> getCart(String userId) {

        log.info("Fetching cart | userId={}", userId);

        return cartRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new CartNotFoundException()))
                .flatMap(this::buildCartResponse);
    }

    // ================= HELPER METHODS =================

    private Mono<ProductResponse> validateAndReduceStock(ProductResponse product, int quantity) {

        if (product.getStock() < quantity) {
            log.error("Insufficient stock | productId={} available={} requested={}",
                    product.getId(), product.getStock(), quantity);
            return Mono.error(new InsufficientStockException());
        }

        log.info("Reducing stock | productId={} quantity={}", product.getId(), quantity);

        return productClient.reduceStock(product.getId(), quantity)
                .thenReturn(product);
    }

    private Mono<Cart> createCart(String userId) {
        return cartRepository.save(Cart.builder().userId(userId).build())
                .doOnSuccess(c -> log.info("New cart created | cartId={}", c.getId()));
    }

    private Mono<Cart> upsertCartItem(Cart cart, ProductResponse product, int quantity) {

        return cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .flatMap(item -> {
                    log.debug("Item exists → updating quantity");
                    item.setQuantity(item.getQuantity() + quantity);
                    return cartItemRepository.save(item);
                })
                .switchIfEmpty(
                        cartItemRepository.save(
                                CartItem.builder()
                                        .cartId(cart.getId())
                                        .productId(product.getId())
                                        .productName(product.getName())
                                        .price(product.getPrice())
                                        .quantity(quantity)
                                        .build()
                        ).doOnSuccess(i -> log.debug("New item added"))
                )
                .thenReturn(cart);
    }

    private Mono<Void> adjustStock(CartItem item, int newQty) {

        int diff = newQty - item.getQuantity();

        log.info("Adjusting stock | productId={} oldQty={} newQty={}",
                item.getProductId(), item.getQuantity(), newQty);

        if (diff > 0) {
            return productClient.reduceStock(item.getProductId(), diff);
        } else if (diff < 0) {
            return productClient.increaseStock(item.getProductId(), Math.abs(diff));
        }

        return Mono.empty();
    }

    private Mono<Cart> updateOrRemoveItem(Cart cart, CartItem item, int quantity) {

        if (quantity == 0) {
            log.info("Removing item from cart | productId={}", item.getProductId());
            return cartItemRepository.deleteByCartIdAndProductId(cart.getId(), item.getProductId())
                    .thenReturn(cart);
        }

        item.setQuantity(quantity);
        return cartItemRepository.save(item).thenReturn(cart);
    }

    private Mono<CartResponse> buildCartResponse(Cart cart) {

        return cartItemRepository.findByCartId(cart.getId())
                .map(CartMapper::toCartItemResponse)
                .collectList()
                .map(items -> CartMapper.toCartResponse(cart, items));
    }
}