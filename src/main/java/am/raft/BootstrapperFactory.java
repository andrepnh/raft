package am.raft;

import am.raft.node.state.NodeType;

public interface BootstrapperFactory {
  Bootstrapper create(int nodeId, NodeType nodeType);
}
