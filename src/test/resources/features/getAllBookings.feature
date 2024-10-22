Feature: Retrieve all bookings
  Returns paginated booking objects

  Background: There are bookings for testing available in the database
    Given there are 5 test bookings in the database

  Scenario: Retrieve bookings (Success)
    When the endpoint "/bookings" is called to get bookings
    Then the response status code 200 should be returned

