@user @login
Feature: User Login

  Background:
    Given base url is "/api/v1/users"

  # ================= POSITIVE =================

  Scenario Outline: Successful login
    Given a registered user with email "<email>" and password "<pass>"
    When I send POST request to "/login" with body:
      | email   | password |
      | <email> | <pass>   |
    Then response status should be 200
    And response should contain "token"

    Examples:
      | email                     | pass      |
      | login_user1@test.com      | password  |
      | login_user2@test.com      | password1 |

  # ================= NEGATIVE =================

  Scenario Outline: Invalid password
    Given a registered user with email "<email>" and password "correct123"
    When I send POST request to "/login" with body:
      | email   | password |
      | <email> | <pass>   |
    Then response status should be 401
    And response should contain "error"

    Examples:
      | email                  | pass       |
      | invalid_user@test.com  | wrong123   |

  Scenario Outline: Login with invalid input
    When I send POST request to "/login" with body:
      | email   | password |
      | <email> | <pass>   |
    Then response status should be 400

    Examples:
      | email        | pass     |
      |              | password |
      | invalid      | password |
      | test@test.com|          |