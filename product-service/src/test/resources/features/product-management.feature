@product @crud
Feature: Product CRUD APIs

  Background:
    Given base url is "/api/v1/products"

  # ================= CREATE =================

  Scenario Outline: Create product successfully
    When I send POST request to "" with body:
      | name   | description | price | stock |
      | <name> | <desc>      | <price> | <stock> |
    Then response status should be 201
    And response should contain "name" as "<name>"

    Examples:
      | name    | desc     | price | stock |
      | iPhone  | Apple    | 1000  | 10    |
      | Pixel   | Google   | 900   | 5     |

  Scenario Outline: Create product validation failure
    When I send POST request to "" with body:
      | name   | description | price | stock |
      | <name> | <desc>      | <price> | <stock> |
    Then response status should be 400

    Examples:
      | name | desc  | price | stock |
      |      | desc  | 100   | 10    |
      | test |       | 100   | 10    |
      | test | desc  |       | 10    |
      | test | desc  | 100   |       |

  # ================= GET =================

  Scenario: Get product by id successfully
    Given a product exists
    When the client calls GET "/{id}" using stored product id
    Then response status should be 200

  Scenario Outline: Get product not found
    When the client calls GET "/<id>"
    Then response status should be 500

    Examples:
      | id   |
      | 999  |
      | 8888 |

  Scenario: Get all products
    Given a product exists
    When the client calls GET ""
    Then response status should be 200
    And response should contain list

  Scenario: Get all products empty
    When the client calls GET ""
    Then response status should be 200

  # ================= UPDATE =================

  Scenario Outline: Update product successfully
    Given a product exists
    When I send PUT request to "/{id}" with body:
      | name   | description | price | stock |
      | <name> | <desc>      | <price> | <stock> |
    Then response status should be 200

    Examples:
      | name      | desc    | price | stock |
      | Updated1  | Desc1   | 2000  | 20    |

  Scenario Outline: Update validation failure
    Given a product exists
    When I send PUT request to "/{id}" with body:
      | name   | description | price | stock |
      | <name> | <desc>      | <price> | <stock> |
    Then response status should be 400

    Examples:
      | name | desc | price | stock |
      |      | desc | 100   | 10    |

  Scenario Outline: Update product not found
    When I send PUT request to "/<id>" with body:
      | name | description | price | stock |
      | test | desc        | 100   | 10    |
    Then response status should be 500

    Examples:
      | id   |
      | 999  |

  # ================= DELETE =================

  Scenario: Delete product successfully
    Given a product exists
    When the client calls DELETE "/{id}" using stored product id
    Then response status should be 204

  Scenario Outline: Delete product not found
    When the client calls DELETE "/<id>"
    Then response status should be 500

    Examples:
      | id   |
      | 999  |