Feature: User Authentication

  Feature: User Authentication

    # REGISTER CASES
    Scenario: Register user successfully
      Given a valid user registration request
      When the client calls register API
      Then the response status should be 201
      And the response should contain email

    Scenario: Register with existing email
      Given a user already exists with email "duplicate@gmail.com"
      When the client calls register API
      Then the response status should be 400
      And the response should contain error code "USER_ALREADY_EXISTS"

    Scenario: Register with invalid input
      Given an invalid user registration request
      When the client calls register API
      Then the response status should be 400

    Scenario: Register with weak password
      Given a user registration request with password "123"
      When the client calls register API
      Then the response status should be 400
      And the response should contain error code "VALIDATION_ERROR"

    # LOGIN CASES
    Scenario: Login successfully
      Given a registered user with email "login@gmail.com" and password "123456"
      When the client calls login API with email "login@gmail.com" and password "123456"
      Then the response status should be 200
      And the response should contain token

    Scenario: Login with invalid password
      Given a registered user with email "login2@gmail.com" and password "123456"
      When the client calls login API with email "login2@gmail.com" and password "wrongpass"
      Then the response status should be 401
      And the response should contain error code "INVALID_CREDENTIALS"

    Scenario: Login with non-existing user
      Given no user exists with email "nouser@gmail.com"
      When the client calls login API with email "nouser@gmail.com" and password "123456"
      Then the response status should be 401

    Scenario: Login with invalid input
      Given an invalid login request
      When the client calls login API
      Then the response status should be 400