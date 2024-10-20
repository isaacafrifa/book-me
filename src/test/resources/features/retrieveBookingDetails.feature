Feature: Retrieve booking details
  Returns a booking object

  Background: There are bookings for testing available in the database
    Given there are 5 testing bookings in the database

#  @RemoveBookingHook
  Scenario: Retrieve a booking by ID (Success)
    Given a booking exists with ID 1 in the database
    When the endpoint "/bookings/1" is called to get a booking
