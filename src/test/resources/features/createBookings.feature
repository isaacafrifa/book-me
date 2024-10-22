Feature: Retrieve booking details
  Returns a booking object

  Background: There are bookings for testing available in the database
    Given there are 5 test bookings in the database

#  @RemoveBookingHook
#  Scenario: Successfully create a booking
#    Given a request with the following fields is to be posted
#      | comments | Another comment |
#    When the endpoint "/bookings" is called to post a booking
#    Then the response status code 201 should be returned
#    And there are 6 testing bookings in the database
#    And the response body contains the created booking details