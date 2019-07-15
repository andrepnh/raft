package am.raft;

public interface ContextFactory {
  Context create(int nodeId);
}
