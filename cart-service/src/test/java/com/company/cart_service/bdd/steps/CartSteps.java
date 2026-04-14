package com.company.cart_service.bdd.steps;

import com.company.cart_service.bdd.config.TestContext;
import com.company.cart_service.client.ProductClient;
import com.company.common.dto.product.response.ProductResponse;

import com.company.cart_service.exception.CustomException;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CartSteps {

    @LocalServerPort
    private int port;

    private WebTestClient client;

    @Autowired
    private TestContext context;

    @Autowired
    private ProductClient productClient;

    private void init() {
        if (client == null) {
            client = WebTestClient.bindToServer()
                    .baseUrl("http://localhost:" + port)
                    .build();
        }
    }

    // ================= GIVEN =================

    @Given("a valid user with role {string}")
    public void validUser(String role) {
        init();
        context.setUserId(1L);
        context.setRole(role);
    }

    @Given("a user with role {string}")
    public void invalidUser(String role) {
        init();
        context.setUserId(1L);
        context.setRole(role);
    }

    @Given("a product exists with stock {int}")
    public void productExists(int stock) {

        init();

        context.setProductId(1L);

        when(productClient.getProduct(anyLong()))
                .thenReturn(Mono.just(
                        ProductResponse.builder()
                                .id(context.getProductId())
                                .name("MockProduct")
                                .price(1000.0)
                                .stock(stock)
                                .build()
                ));

        when(productClient.reduceStock(anyLong(), anyInt()))
                .thenReturn(Mono.empty());

        when(productClient.increaseStock(anyLong(), anyInt()))
                .thenReturn(Mono.empty());
    }

    @Given("a valid add to cart request with quantity {int}")
    public void addToCartRequest(int qty) {
        context.setQuantity(qty);
    }

    @Given("a request with product id {long} and quantity {int}")
    public void invalidProductRequest(Long productId, int qty) {

        context.setProductId(productId);
        context.setQuantity(qty);

        when(productClient.getProduct(productId))
                .thenReturn(Mono.error(
                        new CustomException("Product not found", "PRODUCT_NOT_FOUND", 404)
                ));
    }

    @Given("a cart exists with product and quantity {int}")
    public void cartExists(int qty) {

        productExists(10);
        context.setQuantity(qty);

        addToCart();
    }

    // ================= WHEN =================

    @When("the client calls add to cart API")
    public void addToCart() {

        init();

        Map<String, Object> request = Map.of(
                "productId", context.getProductId(),
                "quantity", context.getQuantity()
        );

        context.setResponse(
                client.post()
                        .uri("/api/v1/cart/add")
                        .header("X-User-Id", context.getUserId().toString())
                        .header("X-User-Role", context.getRole())
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .exchange()
        );
    }

    @When("the client updates cart quantity to {int}")
    public void updateCart(int qty) {

        init();

        Map<String, Object> request = Map.of(
                "productId", context.getProductId(),
                "quantity", qty
        );

        context.setResponse(
                client.put()
                        .uri("/api/v1/cart/update")
                        .header("X-User-Id", context.getUserId().toString())
                        .header("X-User-Role", context.getRole())
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .exchange()
        );
    }

    @When("the client calls get cart API")
    public void getCart() {

        init();

        context.setResponse(
                client.get()
                        .uri("/api/v1/cart")
                        .header("X-User-Id", context.getUserId().toString())
                        .header("X-User-Role", context.getRole())
                        .exchange()
        );
    }

    @When("the client removes product from cart")
    public void removeItem() {

        init();

        context.setResponse(
                client.delete()
                        .uri("/api/v1/cart/remove?productId=" + context.getProductId())
                        .header("X-User-Id", context.getUserId().toString())
                        .header("X-User-Role", context.getRole())
                        .exchange()
        );
    }

    // ================= THEN =================

    @Then("the response status should be {int}")
    public void verifyStatus(int status) {
        context.getResponse().expectStatus().isEqualTo(status);
    }

    @Then("the response should contain cart items")
    public void verifyCartItems() {

        context.getResponse()
                .expectBody()
                .jsonPath("$.items").isArray();
    }
}