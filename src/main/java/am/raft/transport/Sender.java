package am.raft.transport;

import am.raft.message.Message;
import am.raft.node.Node;

public interface Sender {
  void sendTo(Node node, Message message);
}
