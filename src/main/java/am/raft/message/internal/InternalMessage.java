package am.raft.message.internal;

import am.raft.message.Message;
import am.raft.node.NodeRef;

/**
 * For messages that are internally used and never sent over the wire.
 */
public interface InternalMessage<T> extends Message<T> {
  @Override
  default NodeRef getSource() {
    return SelfNodeRef.INSTANCE;
  }

  @Override
  default NodeRef getTarget() {
    return SelfNodeRef.INSTANCE;
  }
}
