@user @registration
Feature: User Registration

  Background:
    Given base url is "/api/v1/users"

  # ================= POSITIVE =================

  Scenario Outline: Successful registration
    When I send POST request to "/register" with body:
      | username | email         | password |
      | <name>   | <email>       | <pass>   |
    Then response status should be 201
    And response should contain "email" as "<email>"

    Examples:
      | name  | email                   | pass      |
      | nihar | nihar_unique@test.com   | password  |
      | raj   | raj_unique@test.com     | password1 |

  # ================= NEGATIVE =================

  Scenario Outline: Duplicate email registration should fail
    Given a registered user with email "<email>" and password "<pass>"
    When I send POST request to "/register" with body:
      | username | email   | password |
      | test     | <email> | <pass>   |
    Then response status should be 400
    And response should contain "error"

    Examples:
      | email                | pass     |
      | dup_user@test.com    | password |

  Scenario Outline: Invalid registration input
    When I send POST request to "/register" with body:
      | username | email     | password |
      | <name>   | <email>   | <pass>   |
    Then response status should be 400

    Examples:
      | name  | email         | pass  |
      |       | test@test.com | pass  |
      | user  | invalid       | pass  |
      | user  | test@test.com | 123   |