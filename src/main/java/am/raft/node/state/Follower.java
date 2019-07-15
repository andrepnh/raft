package am.raft.node.state;

import am.raft.message.Message;
import am.raft.message.internal.SimpleInternalMessage;
import am.raft.queue.CoalescableDequeWriter;
import am.raft.timer.TimerHandle;
import com.google.common.collect.Range;

import java.time.Duration;
import java.util.Random;

public class Follower implements NodeState {
  private static final Range<Integer> ELECTION_TIMEOUT = Range.closed(150, 300);

  private final BasicNodeState delegate;

  public Follower(TimerHandle timerHandle, CoalescableDequeWriter dequeWriter) {
    delegate = new BasicNodeState(timerHandle, dequeWriter);
    var rng = new Random();
    timerHandle.program(this::queueElectionTrigger, () -> {
      var delta = ELECTION_TIMEOUT.upperEndpoint() - ELECTION_TIMEOUT.lowerEndpoint();
      return Duration.ofMillis(rng.nextInt(delta) + ELECTION_TIMEOUT.lowerEndpoint());
    });
  }

  @Override
  public void start() {
    delegate.start();
  }

  @Override
  public boolean consume(Message message) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() throws Exception {
    delegate.close();
  }

  private void queueElectionTrigger() {
    delegate.getWriter().coalesceIntoFirst(SimpleInternalMessage.TRIGGER_ELECTION);
  }
}
