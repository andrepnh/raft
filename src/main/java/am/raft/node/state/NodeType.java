package am.raft.node.state;

import am.raft.queue.CoalescableDequeWriter;
import am.raft.timer.TimerHandle;

import java.util.function.BiFunction;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

public enum NodeType {
  FOLLOWER(Follower::new, nodeState -> nodeState instanceof Follower),
  CANDIDATE(Candidate::new, nodeState -> nodeState instanceof Candidate),
  LEADER(Leader::new, nodeState -> nodeState instanceof Leader);

  private final BiFunction<TimerHandle, CoalescableDequeWriter, ? extends NodeState> constructor;

  private final Predicate<NodeState> typePredicate;

  public static NodeType of(String type) {
    var lowerCaseType = checkNotNull(type).strip().toLowerCase();
    // We always use startsWith in order to support plural
    if (lowerCaseType.startsWith("follower")) {
      return FOLLOWER;
    } else if (lowerCaseType.startsWith("candidate")) {
      return CANDIDATE;
    } else if (lowerCaseType.startsWith("leader")) {
      return LEADER;
    } else {
      throw new IllegalArgumentException("Unknown node type: " + type);
    }
  }

  NodeType(
      BiFunction<TimerHandle, CoalescableDequeWriter, ? extends NodeState> constructor,
      Predicate<NodeState> typePredicate) {
    this.constructor = constructor;
    this.typePredicate = typePredicate;
  }

  public BiFunction<TimerHandle, CoalescableDequeWriter, ? extends NodeState> getConstructor() {
    return constructor;
  }

  public Predicate<NodeState> getTypePredicate() {
    return typePredicate;
  }
}
