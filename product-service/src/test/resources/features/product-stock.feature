@product @stock
Feature: Product Stock Management APIs

  Background:
    Given base url is "/api/v1/products"

  # ================= REDUCE STOCK =================

  Scenario Outline: Reduce stock successfully
    Given a product exists
    When the client calls PUT "/{id}/reduce/<qty>"
    Then response status should be 204

    Examples:
      | qty |
      | 1   |
      | 2   |
      | 5   |

  Scenario Outline: Reduce stock insufficient
    Given a product exists
    When the client calls PUT "/{id}/reduce/<qty>"
    Then response status should be 400

    Examples:
      | qty |
      | 100 |
      | 999 |

  Scenario Outline: Reduce stock invalid quantity
    Given a product exists
    When the client calls PUT "/{id}/reduce/<qty>"
    Then response status should be 400

    Examples:
      | qty |
      | 0   |
      | -1  |

  # ================= INCREASE STOCK =================

  Scenario Outline: Increase stock successfully
    Given a product exists
    When the client calls PUT "/{id}/increase/<qty>"
    Then response status should be 204

    Examples:
      | qty |
      | 1   |
      | 5   |
      | 10  |

  Scenario Outline: Increase stock invalid quantity
    Given a product exists
    When the client calls PUT "/{id}/increase/<qty>"
    Then response status should be 400

    Examples:
      | qty |
      | 0   |
      | -5  |