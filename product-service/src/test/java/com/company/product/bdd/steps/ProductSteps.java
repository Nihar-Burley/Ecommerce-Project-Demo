package com.company.product.bdd.steps;

import com.company.product.bdd.config.TestContext;
import com.company.product.dto.request.ProductRequest;

import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ProductSteps {

    @LocalServerPort
    private int port;

    private WebTestClient client;

    @Autowired
    private TestContext context;

    private void init() {
        if (client == null) {
            client = WebTestClient.bindToServer()
                    .baseUrl("http://localhost:" + port)
                    .build();
        }
    }

    // ================= GIVEN =================

    @Given("a valid product request")
    public void validProductRequest() {
        init();
        context.setName("Product-" + System.currentTimeMillis());
        context.setPrice(1000.0);
        context.setStock(10);
    }

    @Given("a product exists")
    public void productExists() {

        init();

        ProductRequest req = new ProductRequest();
        req.setName("Product-" + System.currentTimeMillis());
        req.setPrice(1000.0);
        req.setStock(10);

        client.post()
                .uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectBody(Map.class)
                .consumeWith(res -> {
                    Map body = res.getResponseBody();
                    context.setProductId(Long.valueOf(body.get("id").toString()));
                });
    }

    @Given("products exist in the system")
    public void productsExist() {
        productExists();
    }

    @Given("a product exists with stock {int}")
    public void productWithStock(int stock) {

        init();

        ProductRequest req = new ProductRequest();
        req.setName("StockProduct-" + System.currentTimeMillis());
        req.setPrice(500.0);
        req.setStock(stock);

        client.post()
                .uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectBody(Map.class)
                .consumeWith(res -> {
                    Map body = res.getResponseBody();
                    context.setProductId(Long.valueOf(body.get("id").toString()));
                });
    }

    // ================= WHEN =================

    @When("the client calls create product API")
    public void createProduct() {

        init();

        ProductRequest req = new ProductRequest();
        req.setName(context.getName());
        req.setPrice(context.getPrice());
        req.setStock(context.getStock());

        context.setResponse(
                client.post()
                        .uri("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(req)
                        .exchange()
        );
    }

    @When("the client calls get product API with stored product id")
    public void getProduct() {

        init();

        context.setResponse(
                client.get()
                        .uri("/api/v1/products/" + context.getProductId())
                        .exchange()
        );
    }

    @When("the client calls get product API with id {long}")
    public void getProductById(Long id) {

        init();

        context.setResponse(
                client.get()
                        .uri("/api/v1/products/" + id)
                        .exchange()
        );
    }

    @When("the client calls get all products API")
    public void getAllProducts() {

        init();

        context.setResponse(
                client.get()
                        .uri("/api/v1/products")
                        .exchange()
        );
    }

    @When("the client calls update product API with stored product id")
    public void updateProduct() {

        init();

        ProductRequest req = new ProductRequest();
        req.setName("Updated-" + context.getName());
        req.setPrice(2000.0);
        req.setStock(20);

        context.setResponse(
                client.put()
                        .uri("/api/v1/products/" + context.getProductId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(req)
                        .exchange()
        );
    }

    @When("the client calls update product API with id {long}")
    public void updateProductNotFound(Long id) {

        init();

        ProductRequest req = new ProductRequest();
        req.setName("Updated");
        req.setPrice(2000.0);
        req.setStock(20);

        context.setResponse(
                client.put()
                        .uri("/api/v1/products/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(req)
                        .exchange()
        );
    }

    @When("the client calls delete product API with stored product id")
    public void deleteProduct() {

        init();

        context.setResponse(
                client.delete()
                        .uri("/api/v1/products/" + context.getProductId())
                        .exchange()
        );
    }

    @When("the client calls delete product API with id {long}")
    public void deleteProductNotFound(Long id) {

        init();

        context.setResponse(
                client.delete()
                        .uri("/api/v1/products/" + id)
                        .exchange()
        );
    }

    // ================= STOCK =================

    @When("the client reduces stock by {int}")
    public void reduceStock(int qty) {

        init();

        context.setResponse(
                client.put()
                        .uri("/api/v1/products/" + context.getProductId() + "/reduce/" + qty)
                        .exchange()
        );
    }

    @When("the client reduces stock for product id {long} by {int}")
    public void reduceStockNotFound(Long id, int qty) {

        init();

        context.setResponse(
                client.put()
                        .uri("/api/v1/products/" + id + "/reduce/" + qty)
                        .exchange()
        );
    }

    @When("the client increases stock by {int}")
    public void increaseStock(int qty) {

        init();

        context.setResponse(
                client.put()
                        .uri("/api/v1/products/" + context.getProductId() + "/increase/" + qty)
                        .exchange()
        );
    }

    @When("the client increases stock for product id {long} by {int}")
    public void increaseStockNotFound(Long id, int qty) {

        init();

        context.setResponse(
                client.put()
                        .uri("/api/v1/products/" + id + "/increase/" + qty)
                        .exchange()
        );
    }

    // ================= THEN =================

    @Then("the response status should be {int}")
    public void verifyStatus(int status) {
        context.getResponse().expectStatus().isEqualTo(status);
    }

    @Then("the response should contain product name")
    public void verifyProductName() {

        context.getResponse()
                .expectBody()
                .jsonPath("$.name").isEqualTo(context.getName());
    }

    @Then("the response should contain stored product id")
    public void verifyProductId() {

        context.getResponse()
                .expectBody()
                .jsonPath("$.id").isEqualTo(context.getProductId());
    }

    @Then("the response should contain list of products")
    public void verifyProductsList() {

        context.getResponse()
                .expectBodyList(Object.class)
                .consumeWith(res -> {
                    assertNotNull(res.getResponseBody());
                    assertTrue(res.getResponseBody().size() > 0);
                });
    }
}
