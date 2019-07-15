package am.raft;

import am.raft.node.Node;
import am.raft.node.state.NodeType;
import am.raft.node.state.StateMachine;
import am.raft.queue.CoalescableDeque;
import am.raft.queue.QueueGuiceModule;
import am.raft.timer.TimerFactoryProvider;
import am.raft.timer.TimerGuiceModule;
import com.google.inject.Guice;
import com.google.inject.assistedinject.Assisted;

import javax.inject.Inject;
import javax.inject.Provider;

public class Bootstrapper {
  private final Context context;

  private final Node node;

  @Inject
  public Bootstrapper(TimerFactoryProvider timerFactoryProvider,
                      Provider<CoalescableDeque> dequeProvider,
                      ContextFactory contextFactory,
                      @Assisted int nodeId,
                      @Assisted NodeType nodeType) {
    this.context = contextFactory.create(nodeId);
    var nodeTimerFactory = timerFactoryProvider.provide(context);
    var deque = dequeProvider.get();
    var currentState = nodeType.getConstructor().apply(nodeTimerFactory.create(), deque);
    var stateMachine = new StateMachine(nodeTimerFactory, deque, context, currentState);
    this.node = new Node(nodeId, deque, stateMachine, context);
  }

  public static void main(String[] args) {
    var nodeId = Integer.parseInt(args[0]);
    var injector = Guice.createInjector(new BootstrapperModule(), new QueueGuiceModule(), new TimerGuiceModule());
    var bootstrapperFactory = injector.getInstance(BootstrapperFactory.class);
    var bootstrapper = bootstrapperFactory.create(nodeId, NodeType.FOLLOWER);
    bootstrapper.start();
  }

  public void start() {
    context.applyTo(node::start).run();
  }

  public Context getContext() {
    return context;
  }

  public Node getNode() {
    return node;
  }
}
