package am.raft.node.state;

import am.raft.message.Message;
import am.raft.queue.CoalescableDequeWriter;
import am.raft.timer.TimerHandle;

public class Leader implements NodeState {
  private final BasicNodeState delegate;

  public Leader(TimerHandle timerHandle, CoalescableDequeWriter dequeWriter) {
    delegate = new BasicNodeState(timerHandle, dequeWriter);
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
}
