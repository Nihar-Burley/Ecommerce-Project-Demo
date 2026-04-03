Feature: Cart Update

  Scenario: Update cart quantity successfully
    Given a cart exists with product and quantity 2
    When the client updates cart quantity to 5
    Then the response status should be 200

  Scenario: Update cart to zero (remove item)
    Given a cart exists with product and quantity 2
    When the client updates cart quantity to 0
    Then the response status should be 200
