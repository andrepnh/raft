package am.raft.node.state;

import am.raft.Context;
import am.raft.message.Message;
import am.raft.message.MessageConsumer;
import am.raft.message.internal.SimpleInternalMessage;
import am.raft.queue.CoalescableDequeWriter;
import am.raft.timer.TimerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

public class StateMachine implements MessageConsumer {
  private static final Logger LOG = LogManager.getLogger(StateMachine.class);

  private final TimerFactory timerFactory;

  private final CoalescableDequeWriter dequeWriter;

  private final Context context;

  private volatile NodeState currentState;

  public StateMachine(TimerFactory timerFactory, CoalescableDequeWriter dequeWriter, Context context) {
    this(timerFactory, dequeWriter, context, new Follower(timerFactory.create(), dequeWriter));
  }

  // TODO figure out another way to inject current state when testing
  public StateMachine(TimerFactory timerFactory,
                      CoalescableDequeWriter dequeWriter,
                      Context context,
                      NodeState state) {
    this.timerFactory = checkNotNull(timerFactory);
    this.dequeWriter = checkNotNull(dequeWriter);
    this.context = checkNotNull(context);
    transitionTo(state);
  }

  public void start() {
    currentState.start();
  }

  @Override
  public boolean consume(Message<?> message) {
    if (message.getKind() == SimpleInternalMessage.TRIGGER_ELECTION) {
      transitionTo(new Candidate(timerFactory.create(), dequeWriter));
      return currentState.consume(message);
    } else {
      return currentState.consume(message);
    }
  }

  public NodeState getCurrentState() {
    return currentState;
  }

  public TimerFactory getTimerFactory() {
    return timerFactory;
  }

  private void transitionTo(NodeState target) {
    closeSilently(currentState);
    var before = Optional.ofNullable(currentState).map(state -> state.getClass().getSimpleName()).orElse("null");
    currentState = target;
    context.replaceCurrentState(target.getClass());
    LOG.info("Transitioned from {} to {}", before, target.getClass().getSimpleName());
  }

  private void closeSilently(NodeState currentState) {
    if (currentState != null) {
      try {
        currentState.close();
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    }
  }

}
