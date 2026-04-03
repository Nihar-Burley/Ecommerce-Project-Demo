Feature: User Management

  # GET USER
  Scenario: Get user by ID successfully
    Given a registered user with email "user1@gmail.com" and password "123456"
    When the client calls get user API with stored user id
    Then the response status should be 200
    And the response should contain stored user id

  Scenario: Get user by ID not found
    Given no user exists with id 999
    When the client calls get user API with id 999
    Then the response status should be 404
    And the response should contain error code "USER_NOT_FOUND"

  # GET ALL USERS
  Scenario: Get all users successfully
    Given users exist in the system
    When the client calls get all users API
    Then the response status should be 200
    And the response should contain list of users

  Scenario: Get all users when no users exist
    Given no users exist in the system
    When the client calls get all users API
    Then the response status should be 200

  # DELETE USER
  Scenario: Delete user successfully
    Given a registered user with email "delete@gmail.com" and password "123456"
    When the client calls delete user API with stored user id
    Then the response status should be 204

  Scenario: Delete user not found
    Given no user exists with id 999
    When the client calls delete user API with id 999
    Then the response status should be 404
    And the response should contain error code "USER_NOT_FOUND"