Feature: User Management APIs

  Background:
    Given base url is "/api/v1/users"

  # ================= GET USER =================

  Scenario: Get user by ID successfully
    Given a registered user with email "user1@gmail.com" and password "123456"
    When the client calls GET "/{id}" using stored user id
    Then response status should be 200
    And response should contain "id" as stored user id

  Scenario Outline: Get user by ID not found
    When the client calls GET "/<id>"
    Then response status should be 404
    And response should contain "error" as "USER_NOT_FOUND"

    Examples:
      | id   |
      | 999  |
      | 8888 |

  # ================= GET ALL =================

  Scenario: Get all users successfully
    Given users exist in the system
    When the client calls GET ""
    Then response status should be 200
    And response should contain list

  Scenario: Get all users when no users exist
    Given no users exist in the system
    When the client calls GET ""
    Then response status should be 200
    And response should contain empty list

  # ================= DELETE =================

  Scenario: Delete user successfully
    Given a registered user with email "delete@gmail.com" and password "123456"
    When the client calls DELETE "/{id}" using stored user id
    Then response status should be 204

  Scenario Outline: Delete user not found
    When the client calls DELETE "/<id>"
    Then response status should be 404
    And response should contain "error" as "USER_NOT_FOUND"

    Examples:
      | id   |
      | 999  |
      | 7777 |