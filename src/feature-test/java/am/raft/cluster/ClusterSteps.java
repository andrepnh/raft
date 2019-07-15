package am.raft.cluster;


import am.raft.fixture.Environment;
import am.raft.node.state.NodeType;
import am.raft.fixture.TestNodeFactory;
import am.raft.node.Node;
import am.raft.node.state.NodeState;
import cucumber.runtime.java.guice.ScenarioScoped;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.assertj.core.api.Condition;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.assertj.core.api.Assertions.assertThat;

@ScenarioScoped
public class ClusterSteps {
  private final Environment environment;

  private final TestNodeFactory testNodeFactory;

  @Inject
  public ClusterSteps(Environment environment, TestNodeFactory testNodeFactory) {
    this.environment = checkNotNull(environment);
    this.testNodeFactory = checkNotNull(testNodeFactory);
  }

  @Given("{int} {nodeType} node(s)")
  public void givenNodes(int amount, NodeType type) {
    List<Node> nodes = Stream.generate(() -> testNodeFactory.ofType(type))
        .limit(amount)
        .collect(Collectors.toList());
    environment.setClusterNodes(nodes);
  }

  @Then("at least {int} node(s) is/are a {nodeType}")
  public void atLeastNNodesOfType(int nNodes, NodeType type) {
    List<NodeState> clusterNodeStates = environment.getClusterNodes()
        .stream()
        .map(node -> node.getStateMachine().getCurrentState())
        .collect(Collectors.toList());
    var ofType = new Condition<>(type.getTypePredicate(), "Node match type: " + type);
    assertThat(clusterNodeStates).areAtLeast(nNodes, ofType);
  }
}
