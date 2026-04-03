Feature: Product Stock

  Scenario: Reduce stock successfully
    Given a product exists with stock 10
    When the client reduces stock by 2
    Then the response status should be 204

  Scenario: Reduce stock with insufficient quantity
    Given a product exists with stock 5
    When the client reduces stock by 10
    Then the response status should be 400

  Scenario: Reduce stock for non-existing product
    When the client reduces stock for product id 999 by 2
    Then the response status should be 404

  Scenario: Increase stock successfully
    Given a product exists with stock 5
    When the client increases stock by 5
    Then the response status should be 204

  Scenario: Increase stock for non-existing product
    When the client increases stock for product id 999 by 5
    Then the response status should be 404