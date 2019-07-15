package am.raft.node.state;

import am.raft.message.Message;
import am.raft.queue.CoalescableDequeWriter;
import am.raft.timer.TimerHandle;

import static com.google.common.base.Preconditions.checkNotNull;

class BasicNodeState implements NodeState {
  private final TimerHandle timerHandle;

  private final CoalescableDequeWriter dequeWriter;

  BasicNodeState(TimerHandle timerHandle, CoalescableDequeWriter dequeWriter) {
    this.timerHandle = checkNotNull(timerHandle);
    this.dequeWriter = checkNotNull(dequeWriter);
  }

  @Override
  public void start() {
    timerHandle.start();
  }

  @Override
  public boolean consume(Message message) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() throws Exception {
    timerHandle.cancel();
  }

  public CoalescableDequeWriter getWriter() {
    return dequeWriter;
  }
}
