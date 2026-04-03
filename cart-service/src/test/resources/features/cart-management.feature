Feature: Cart Management

  Scenario: Add item to cart successfully
    Given a valid user with role "USER"
    And a product exists with stock 10
    And a valid add to cart request with quantity 2
    When the client calls add to cart API
    Then the response status should be 200
    And the response should contain cart items

  Scenario: Add item with insufficient stock
    Given a valid user with role "USER"
    And a product exists with stock 2
    And a valid add to cart request with quantity 5
    When the client calls add to cart API
    Then the response status should be 400

  Scenario: Add item for non-existing product
    Given a valid user with role "USER"
    And a request with product id 999 and quantity 2
    When the client calls add to cart API
    Then the response status should be 404