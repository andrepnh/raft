package am.raft.node.state;

import am.raft.message.MessageConsumer;

public interface NodeState extends AutoCloseable, MessageConsumer {
  /**
   * Activates timers, etc.
   */
  void start();
}
