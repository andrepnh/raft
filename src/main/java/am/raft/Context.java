package am.raft;

import am.raft.node.state.NodeState;
import com.google.inject.assistedinject.Assisted;
import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.ThreadContext;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Context {
  private final int id;

  private volatile Class<? extends NodeState> currentState;

  @Inject
  public Context(@Assisted int id) {
    this.id = id;
  }

  /**
   * Used to add diagnostic information held by this context to logs made by {@code action}.
   */
  public Runnable applyTo(Runnable action) {
    return () -> {
      try (var ctc = CloseableThreadContext
          .put("node", String.valueOf(id))
          .put("state", currentState == null ? "null" : currentState.getSimpleName())) {
        action.run();
      }
    };
  }

  public void replaceCurrentState(Class<? extends NodeState> currentState) {
    this.currentState = checkNotNull(currentState);
    ThreadContext.put("state", currentState.getSimpleName());
  }

  public int getId() {
    return id;
  }
}
