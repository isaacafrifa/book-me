Feature: Create bookings
  Returns a booking object

  Background: There are bookings for testing available in the database
    Given there are 5 test bookings in the database


  @RemoveBookings
  Scenario: Successfully create a booking
    Given a request with the following fields is to be posted
      | comments | Another comment |
    When the endpoint "/bookings" is called to post a booking
    Then the response status code 201 should be returned
    And there are 6 bookings in the database
    And the response body contains the following booking details
      | bookingId     | 6                 |
      | userId        | 1                 |
      | startTime     | 2030-08-01T10:00Z |
      | bookingStatus | Pending           |
      | comments      | Another comment   |

  Scenario Outline: Create booking with missing or invalid fields
    Given a request with the following fields is to be posted
      | <field> | <input> |
    When the endpoint "/bookings" is called to post a booking
    Then the response status code 400 should be returned
    And the response should contain the error message "<expected_message>"
    Examples:
      | field     | input             | expected_message                                                                        |
      | startTime |                   | startTime must not be null                                                              |
      # Booking startTime should be in the future
      | startTime | 2020-08-01T10:00Z | Invalid request argument                                                                |
#      | startTime | 2023-13-45T99:99Z | Invalid date format             |
      | userEmail | john              | userEmail size must be between 10 and 50, userEmail must be a well-formed email address |
      | userEmail | johnDoe@com       | Invalid request argument                                                                |
      | userEmail | test@@domain.com  | userEmail must be a well-formed email address                                           |
      | userEmail | @Home Street      | userEmail must be a well-formed email address                                           |
      | userEmail | user@domain#com   | Invalid request argument                                                                |
      | userEmail |                   | userEmail must not be null                                                              |


  @RemoveBookings
  Scenario: Create booking with different timezone
    Given a request with the following fields is to be posted
      | startTime | 2035-08-01T10:00+05:30 |
    When the endpoint "/bookings" is called to post a booking
    Then the response status code 201 should be returned
    And the response body contains the following booking details
      # startTime converted to UK time
      | startTime     | 2035-08-01T04:30Z |
      | bookingStatus | Pending           |

#  Scenario: Create booking with time slot conflict
#    Given a booking exists for "2023-08-01T10:00Z"
#    When a request with the following fields is to be posted
#      | startTime | 2023-08-01T10:00Z |
#    Then the response status code 409 should be returned
#    And the error message contains "Time slot already booked"


#  Scenario: Create booking with invalid user
#    Given a request with the following fields is to be posted
#      | userId | 999 |
#    When the endpoint "/bookings" is called to post a booking
#    Then the response status code 404 should be returned
#    And the error message contains "User not found"


#  Scenario: Create multiple bookings in quick succession
#    Given 3 concurrent booking requests for the same time slot
#    When the endpoint "/bookings" is called for all requests simultaneously
#    Then only one booking should be created
#    And other requests should receive 409 status code



