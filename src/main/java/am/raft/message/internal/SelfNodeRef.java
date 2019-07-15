package am.raft.message.internal;

import am.raft.node.NodeRef;

public enum SelfNodeRef implements NodeRef {
  INSTANCE;

  @Override
  public int getId() {
    return 0;
  }
}
