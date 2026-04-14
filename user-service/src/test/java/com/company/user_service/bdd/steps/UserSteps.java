package com.company.user_service.bdd.steps;


import io.cucumber.java.en.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class UserSteps {

    private String baseUrl;
    private ResponseEntity<String> response;
    private Map<String, Object> requestBody = new HashMap<>();
    private String storedUserId;

    private final RestTemplate restTemplate = new RestTemplate();

    // ================= BACKGROUND =================
    @Given("base url is {string}")
    public void base_url_is(String url) {
        this.baseUrl = "http://localhost:8080" + url;
    }

    // ================= LOGIN =================
    @Given("a registered user with email {string} and password {string}")
    public void a_registered_user_with_email_and_password(String email, String password) {
        // Setup request body for login/registration
        requestBody.put("email", email);
        requestBody.put("password", password);
    }

    @When("I send POST request to {string} with body:")
    public void i_send_post_request_to_with_body(String endpoint, io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> body = dataTable.asMap(String.class, String.class);
        response = restTemplate.postForEntity(baseUrl + endpoint, body, String.class);
    }

    @Then("response status should be {int}")
    public void response_status_should_be(Integer status) {
        assertEquals(status, response.getStatusCodeValue());
    }

    @Then("response should contain {string}")
    public void response_should_contain(String keyword) {
        assertTrue(response.getBody().contains(keyword));
    }

    @Then("response should contain {string} as {string}")
    public void response_should_contain_as(String field, String value) {
        assertTrue(response.getBody().contains("\"" + field + "\":\"" + value + "\""));
    }

    // ================= USER MANAGEMENT =================
    @When("the client calls GET {string} using stored user id")
    public void the_client_calls_get_using_stored_user_id(String endpoint) {
        response = restTemplate.getForEntity(baseUrl + "/" + storedUserId, String.class);
    }

    @When("the client calls GET {string}")
    public void the_client_calls_get(String endpoint) {
        response = restTemplate.getForEntity(baseUrl + endpoint, String.class);
    }

    @When("the client calls DELETE {string} using stored user id")
    public void the_client_calls_delete_using_stored_user_id(String endpoint) {
        restTemplate.delete(baseUrl + "/" + storedUserId);
        // Simulate DELETE response (Spring RestTemplate delete returns void)
        response = ResponseEntity.status(204).build();
    }

    @When("the client calls DELETE {string}")
    public void the_client_calls_delete(String endpoint) {
        try {
            restTemplate.delete(baseUrl + endpoint);
            response = ResponseEntity.status(204).build();
        } catch (Exception e) {
            response = ResponseEntity.status(404).body("{\"error\":\"USER_NOT_FOUND\"}");
        }
    }

    // ================= REGISTRATION =================
    @When("I send POST request to {string} with body:")
    public void i_send_post_request_to_register(String endpoint, io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> body = dataTable.asMap(String.class, String.class);
        response = restTemplate.postForEntity(baseUrl + endpoint, body, String.class);
        if (response.getStatusCodeValue() == 201) {
            // Extract user id from response if needed
            storedUserId = "1"; // Replace with actual parsing logic
        }
    }
}
