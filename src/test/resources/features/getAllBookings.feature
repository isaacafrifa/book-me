Feature: Retrieve all bookings
  Returns paginated booking objects

  Background: There are bookings for testing available in the database
    Given there are 5 test bookings in the database


 # Get all bookings without pagination values ie. no query params
  Scenario: Retrieve bookings (Success)
    When the endpoint "/bookings" is called to get bookings
    Then the response status code 200 should be returned
    * 5 bookings should be returned


 # Get bookings with pagination values
  Scenario Outline: Retrieve paginated bookings with queryParams (Success)
    When the endpoint "/bookings?pageNo=<pageNo>&pageSize=<pageSize>&direction=<direction>&orderBy=<orderBy>" is called to get bookings
    Then the response status code 200 should be returned
    And <expectedCount> bookings should be returned
    * the bookings should be sorted by "<orderBy>" in "<direction>" order
    Examples:
      | expectedCount | pageNo | pageSize | direction | orderBy   |
      # Basic pagination test
      | 5             | 0      | 30       | asc       | id        |
      | 2             | 1      | 2        | ASC       | id        |
      | 1             | 2      | 2        | asc       | id        |
      # Sort direction test
      | 5             | 0      | 10       | desc      | id        |
      | 5             | 0      | 6        | DESC      | id        |
      # Different sort fields
      | 5             | 0      | 10       | asc       | startTime |
      | 5             | 0      | 10       | asc       | status    |
      | 5             | 0      | 10       | asc       | createdOn |
      | 5             | 0      | 10       | desc      | createdOn |
      # Edge cases
      # Beyond available data
      | 0             | 99     | 10       | asc       | id        |
      # Minimum page size
      | 1             | 0      | 1        | asc       | id        |



  # Get Bookings with Invalid Input
  Scenario Outline: Retrieve paginated bookings with invalid queryParams (Failure)
    When the endpoint "/bookings?pageNo=<pageNo>&pageSize=<pageSize>&direction=<direction>&orderBy=<orderBy>" is called to get bookings
    Then the response status code <statusCode> should be returned
    Examples:
      | statusCode | pageNo | pageSize | direction  | orderBy     |
      # Negative page number
      | 400        | -1     | 10       | asc        | id          |
        # Negative page size
      | 400        | 0      | -5       | asc        | id          |
       # Zero page size
      | 400        | 0      | 0        | asc        | id          |
       # Invalid direction
      | 400        | 0      | 10       | descending | id          |
      # Invalid sort field
      | 400        | 0      | 10       | asc        | nonexistent |


  # Get Bookings with Missing Parameters
  Scenario Outline: Retrieve bookings with missing queryParams (Default Behavior i.e. Success)
    When the endpoint "<endpoint>" is called to get bookings
    Then the response status code 200 should be returned
    And <expectedCount> bookings should be returned
    Examples:
      | expectedCount | endpoint                                     |
       # Missing pageNo
      | 5             | /bookings?pageSize=10                        |
      # Missing pageSize
      | 5             | /bookings?pageNo=0                           |
      # Missing direction
      | 5             | /bookings?pageNo=0&pageSize=10               |
      # Missing orderBy
      | 5             | /bookings?pageNo=0&pageSize=10&direction=asc |




