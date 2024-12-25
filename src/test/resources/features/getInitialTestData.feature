Feature: Retrieve initial test data in the testContainer database

  Background: There are bookings for testing available in the database
    Given the test database is initialized with test data

  Scenario: Verify the content of test bookings
    When I retrieve all bookings from the database
    Then the total number of bookings should be 5
    And the bookings should contain the following data:
      | bookingId | userId | startTime                 | bookingStatus | comments                    |
      | 1         | 1      | 2024-10-11T12:00:00+00:00 | Pending       | This is a pending booking   |
      | 2         | 2      | 2024-10-12T15:00:00+00:00 | Confirmed     | This is a confirmed booking |
      | 3         | 1      | 2024-10-13T10:00:00+00:00 | Cancelled     | This booking was cancelled  |
      | 4         | 3      | 2024-10-14T18:00:00+00:00 | Pending       |                             |
      | 5         | 2      | 2024-10-15T11:00:00+00:00 | Confirmed     | Another confirmed booking   |

