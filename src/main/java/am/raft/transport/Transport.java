package am.raft.transport;

import am.raft.message.Message;

import java.util.function.Consumer;

public interface Transport extends Sender, AutoCloseable {
  void open(Consumer<Message> messageConsumer);
}
