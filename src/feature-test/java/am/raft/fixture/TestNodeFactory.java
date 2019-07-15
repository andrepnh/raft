package am.raft.fixture;

import am.raft.BootstrapperFactory;
import am.raft.node.Node;
import am.raft.node.state.NodeType;
import cucumber.runtime.java.guice.ScenarioScoped;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkNotNull;

@ScenarioScoped
public class TestNodeFactory {
  private final BootstrapperFactory bootstrapperFactory;

  private final AtomicInteger serialId = new AtomicInteger(1);

  @Inject
  public TestNodeFactory(BootstrapperFactory bootstrapperFactory) {
    this.bootstrapperFactory = checkNotNull(bootstrapperFactory);
  }

  public Node ofType(NodeType type) {
    var bootstrapper = bootstrapperFactory.create(serialId.getAndIncrement(), type);
    bootstrapper.start();
    return bootstrapper.getNode();
  }
}
