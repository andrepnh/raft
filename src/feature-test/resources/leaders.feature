@ignore
Feature: Leaders

  Background: all timers will start paused by default


  Scenario: A leader steps down upon finding another one with a higher term
    Given 1 leader and 4 follower nodes
    When I simulate a partition that separates 1 leader and 1 follower away
    And I let the nodes on the bigger partition run for 5 cycles
    Then 1 of those nodes is a leader
    When I select nodes in the bigger partition
    And I heal the partition
    And I let the leader of the selected nodes run for 1 cycle
    Then 1 node is a leader

  Scenario: Followers and leaders discard uncommitted entries upon hearing from an higher term leader
    Given 1 leader and 4 follower nodes
    When I simulate a partition that separates 1 leader and 1 follower away
    And a client sends an unique operation to the leader
    Then the leader's log remains uncommitted
    When I let the nodes on the bigger partition run for 5 cycles
    Then 1 of those nodes is a leader
    When I select 1 leader in the bigger partition
    And I heal the partition
    And a client sends an unique operation to the selected node
    Then 1 node is a leader
    And the leader's log has 1 committed entry
    And all logs are the same

  Scenario: Candidates discard uncommitted entries upon hearing from a leader
    Given 1 leader and 4 follower nodes
    When I simulate a partition that separates 1 leader and 1 follower away
    And a client sends an unique operation to the leader
    And I let 1 follower in the smaller partition run for 1 cycle
    Then that node is a candidate
    And that node's log has 1 uncommitted entry
    When I let the nodes on the bigger partition run for 5 cycles
    And I select 1 leader in the bigger partition
    And I heal the partition
    And a client sends an unique operation to the selected node
    Then 1 node is a leader
    And the leader log has 1 committed entry
    And all logs are the same