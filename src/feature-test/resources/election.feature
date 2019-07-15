Feature: Election

  Background: all timers will start paused by default

  Scenario: Followers becoming candidates
    Given 3 follower nodes
    When I let each timer run for 1 cycle
    Then at least 1 node is a candidate

  @ignore
  Scenario: Other candidates stepping down upon not reaching majority
    Given 3 candidates and 2 follower nodes
    When all candidate timers run for 1 cycle
    Then we have no leaders
    When one candidate timer runs for 1 cycle
    Then that node is now a leader
    And there are no candidates

  @ignore
  Scenario: All current candidates stepping down upon not reaching majority
    Given 3 candidate and 2 follower nodes
    When all candidate timers run for 1 cycle
    Then we have no leaders
    When 1 follower node runs for 1 cycle
    Then that node is now a leader

  @ignore
  Scenario: Candidates stepping down upon hearing from a leader
    Given 1 leader and 4 follower nodes
    When I simulate a partition that separates 2 follower nodes away
    And I let their timers run for 3 cycles
    Then at least 1 of those nodes is a candidate
    When I heal the partition
    And 1 leader node runs for 1 cycle
    Then no nodes are candidates

  @ignore
  Scenario: Candidates stepping down upon hearing from an higher-term candidate
    Given 5 follower nodes
    When I simulate a partition that separates 2 nodes away
    And I let 1 node in the smaller partition run for 10 cycles
    Then that node is a candidate
    When I let the nodes on the bigger partition run for 3 cycles
    Then at least 1 of those nodes is a candidate
    When I select nodes in the smaller partition
    And I heal the partition
    And I let 1 of the selected nodes run for 1 cycle
    Then 1 node is a candidate