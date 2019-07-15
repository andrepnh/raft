package am.raft.fixture;

import am.raft.node.Node;
import com.google.common.collect.ImmutableList;
import cucumber.runtime.java.guice.ScenarioScoped;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

@ScenarioScoped
public class Environment {
  private final List<Node> clusterNodes = new ArrayList<>();

  public void setClusterNodes(Collection<Node> clusterNodes) {
    checkState(this.clusterNodes.isEmpty(),
        "Cannot change cluster clusterNodes once they're set. Current clusterNodes: %s",
        clusterNodes);
    this.clusterNodes.addAll(clusterNodes);
  }

  public ImmutableList<Node> getClusterNodes() {
    return ImmutableList.copyOf(clusterNodes);
  }
}
