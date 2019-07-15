package am.raft.node.state;

import am.raft.message.Message;
import am.raft.message.internal.SimpleInternalMessage;
import am.raft.queue.CoalescableDequeWriter;
import am.raft.timer.TimerHandle;

import static com.google.common.base.Preconditions.checkState;

public class Candidate implements NodeState {
  private final BasicNodeState delegate;

  public Candidate(TimerHandle timerHandle, CoalescableDequeWriter dequeWriter) {
    delegate = new BasicNodeState(timerHandle, dequeWriter);
  }

  @Override
  public void start() {
    delegate.start();
  }

  @Override
  public boolean consume(Message message) {
    checkState(message.getKind() == SimpleInternalMessage.TRIGGER_ELECTION);
    // Nothing to do at the moment
    return true;
  }

  @Override
  public void close() throws Exception {
    delegate.close();
  }
}
