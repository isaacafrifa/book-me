Feature: Retrieve booking details
  Returns a booking object

  Background: There are bookings for testing available in the database
    Given there are 5 test bookings in the database

  Scenario: Retrieve a booking by ID (Success)
    Given a booking exists with ID 1 in the database
    When the endpoint "/bookings/1" is called to get a booking
    Then the response status code 200 should be returned
    And the response body should contain the bookingID 1

  Scenario: Retrieve a booking by ID (Not Found)
    Given no booking exists with id 44
    When the endpoint "/bookings/44" is called to get a booking
    Then the response status code 404 should be returned
    And the response body should contain the message "Booking not found"

  Scenario: Retrieve a booking with an invalid ID
    When the endpoint "/bookings/abc" is called to get a booking
    Then the response status code 400 should be returned
    And the response body should contain the message "Invalid request argument"