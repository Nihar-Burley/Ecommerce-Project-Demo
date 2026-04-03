Feature: Product Management

  Scenario: Create product successfully
    Given a valid product request
    When the client calls create product API
    Then the response status should be 201
    And the response should contain product name

  Scenario: Get product by ID successfully
    Given a product exists
    When the client calls get product API with stored product id
    Then the response status should be 200
    And the response should contain stored product id

  Scenario: Get product by ID not found
    When the client calls get product API with id 999
    Then the response status should be 404

  Scenario: Get all products
    Given products exist in the system
    When the client calls get all products API
    Then the response status should be 200
    And the response should contain list of products

  Scenario: Update product successfully
    Given a product exists
    And a valid product request
    When the client calls update product API with stored product id
    Then the response status should be 200

  Scenario: Update product not found
    Given a valid product request
    When the client calls update product API with id 999
    Then the response status should be 404

  Scenario: Delete product successfully
    Given a product exists
    When the client calls delete product API with stored product id
    Then the response status should be 204

  Scenario: Delete product not found
    When the client calls delete product API with id 999
    Then the response status should be 404