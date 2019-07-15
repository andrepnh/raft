package am.raft.message;

public interface MessageConsumer {
  boolean consume(Message<?> message);
}
