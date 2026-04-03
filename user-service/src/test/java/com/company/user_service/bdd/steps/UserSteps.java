package com.company.user_service.bdd.steps;

import com.company.user_service.bdd.config.TestContext;
import com.company.user_service.dto.request.LoginRequest;
import com.company.user_service.dto.request.RegisterRequest;

import io.cucumber.java.en.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

public class UserSteps {

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

    @Given("a valid user registration request")
    public void validRegisterRequest() {

        init();

        context.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        context.setPassword("123456");
    }

    @Given("a user already exists with email {string}")
    public void userExists(String email) {

        init();

        RegisterRequest req = new RegisterRequest();
        req.setEmail(email);
        req.setUsername("nihar");
        req.setPassword("123456");

        client.post()
                .uri("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange();

        context.setEmail(email); // ✅ IMPORTANT
        context.setPassword("123456");
    }

    @Given("a registered user with email {string} and password {string}")
    public void registeredUser(String email, String password) {

        init();

        RegisterRequest req = new RegisterRequest();
        req.setEmail(email);
        req.setUsername("nihar");
        req.setPassword(password);

        client.post()
                .uri("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id")
                .value(id -> context.setUserId(((Number) id).longValue()));
    }

    @Given("no user exists with id {long}")
    public void noUserExists(Long id) {
        // Nothing required
        // Just ensures we call non-existing ID
    }

    @Given("users exist in the system")
    public void usersExist() {

        init();

        RegisterRequest req = new RegisterRequest();
        req.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        req.setUsername("nihar");
        req.setPassword("123456");

        client.post()
                .uri("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange();
    }

    @Given("no users exist in the system")
    public void noUsersExist() {
        // Ideally reset DB (Testcontainers / cleanup)
        // For now, assume empty DB
    }

    @Given("an invalid user registration request")
    public void invalidRegistrationRequest() {

        init();

        // Missing email & password → invalid
        context.setEmail("");
        context.setPassword("");
    }

    @Given("a user registration request with password {string}")
    public void weakPassword(String password) {

        init();

        context.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        context.setPassword(password);
    }
    @Given("no user exists with email {string}")
    public void noUserExistsWithEmail(String email) {
        init();
        context.setEmail(email);
    }

    @Given("an invalid login request")
    public void invalidLoginRequest() {
        init();
        context.setEmail("");
        context.setPassword("");
    }

    // ================= WHEN =================

    @When("the client calls register API")
    public void callRegister() {

        RegisterRequest req = new RegisterRequest();
        req.setEmail(context.getEmail());
        req.setUsername("nihar");
        req.setPassword(context.getPassword());

        context.setResponse(
                client.post()
                        .uri("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(req)
                        .exchange()
        );
    }

    @When("the client calls login API with email {string} and password {string}")
    public void callLogin(String email, String password) {

        LoginRequest req = new LoginRequest();
        req.setEmail(email);
        req.setPassword(password);

        context.setResponse(
                client.post()
                        .uri("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(req)
                        .exchange()
        );
    }

    @When("the client calls get user API with id {long}")
    public void getUser(Long id) {
        init();
        context.setResponse(
                client.get()
                        .uri("/api/v1/users/" + id)
                        .exchange()
        );
    }

    @When("the client calls get all users API")
    public void getAllUsers() {
        init();
        context.setResponse(
                client.get()
                        .uri("/api/v1/users")
                        .exchange()
        );
    }

    @When("the client calls delete user API with id {long}")
    public void deleteUser(Long id) {
        init();
        context.setResponse(
                client.delete()
                        .uri("/api/v1/users/" + id)
                        .exchange()
        );
    }

    @When("the client calls delete user API with stored user id")
    public void deleteStoredUser() {

        context.setResponse(
                client.delete()
                        .uri("/api/v1/users/" + context.getUserId())
                        .exchange()
        );
    }

    @When("the client calls get user API with stored user id")
    public void getUserStored() {

        context.setResponse(
                client.get()
                        .uri("/api/v1/users/" + context.getUserId())
                        .exchange()
        );
    }

    @When("the client calls login API")
    public void callLoginApi() {

        init();

        LoginRequest req = new LoginRequest();
        req.setEmail(context.getEmail());
        req.setPassword(context.getPassword());

        context.setResponse(
                client.post()
                        .uri("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(req)
                        .exchange()
        );
    }

    // ================= THEN =================

    @Then("the response should contain email {string}")
    public void verifyEmail(String email) {
        context.getResponse()
                .expectBody()
                .jsonPath("$.email").isEqualTo(email);
    }

    @Then("the response should contain token")
    public void verifyToken() {
        context.getResponse()
                .expectBody()
                .jsonPath("$.token").exists();
    }

    @Then("the response status should be {int}")
    public void verifyStatus(int status) {
        context.getResponse().expectStatus().isEqualTo(status);
    }

    @Then("the response should contain error code {string}")
    public void verifyErrorCode(String code) {
        context.getResponse()
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(code);
    }

    @Then("the response should contain user id {long}")
    public void verifyUserId(Long id) {

        context.getResponse()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id);
    }

    @Then("the response should contain stored user id")
    public void verifyStoredUserId() {

        context.getResponse()
                .expectBody()
                .jsonPath("$.id").isEqualTo(context.getUserId());
    }

    @Then("the response should contain list of users")
    public void verifyUsersList() {

        context.getResponse()
                .expectBodyList(Object.class)
                .consumeWith(result -> {
                    assert result.getResponseBody() != null;
                    assert !result.getResponseBody().isEmpty();
                });
    }

    @Then("the response should contain email")
    public void verifyEmail() {

        context.getResponse()
                .expectBody()
                .jsonPath("$.email").isEqualTo(context.getEmail());
    }
}