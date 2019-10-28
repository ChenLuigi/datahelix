Feature: Whilst including non-latin characters, user can specify that a value either matches or contains a specified regex

  Background:
    Given the generation strategy is full
    And there is a non nullable field foo
    And foo has type "string"

  Scenario: Running a 'matchingRegex' request that includes special characters (emoji) only should be successful
    Given foo is matching regex /[😁-😘]{1}/
    Then the following data should be generated:
      | foo |

