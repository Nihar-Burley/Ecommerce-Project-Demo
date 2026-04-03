Feature: Cart Access

  Scenario: Get cart successfully
    Given a cart exists with product and quantity 2
    When the client calls get cart API
    Then the response status should be 200

  Scenario: Remove item from cart
    Given a cart exists with product and quantity 2
    When the client removes product from cart
    Then the response status should be 204

  Scenario: Access denied for invalid role
    Given a user with role "GUEST"
    When the client calls get cart API
    Then the response status should be 403