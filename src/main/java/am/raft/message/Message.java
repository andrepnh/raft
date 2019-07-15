package am.raft.message;

import am.raft.node.NodeRef;

public interface Message<T> {
  NodeRef getSource();

  NodeRef getTarget();

  MessageKind getKind();

  T getContents();
}
