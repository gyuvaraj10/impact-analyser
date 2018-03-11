Feature: Skippine

  Scenario: Test1
    Given I Test SKiped
    Then I failed to test


  Scenario Outline: Test2
    Given I Test "<a>" SKiped
    Then I failed to test
    Examples:
    |a|
    |1|