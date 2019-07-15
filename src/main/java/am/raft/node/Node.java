package am.raft.node;

import am.raft.Context;
import am.raft.message.Message;
import am.raft.message.MessageConsumer;
import am.raft.node.state.StateMachine;
import am.raft.queue.CoalescableDeque;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Node implements NodeRef, MessageConsumer {
  private final int id;

  private final CoalescableDeque messageDeque;

  private final StateMachine stateMachine;

  private final ExecutorService nodeLoopPool;

  private final Context context;

  public Node(int id, CoalescableDeque messageDeque, StateMachine stateMachine, Context context) {
    checkArgument(id > 0, "Node id has to be greater than 0; got %s", id);
    this.id = id;
    this.messageDeque = checkNotNull(messageDeque);
    this.stateMachine = checkNotNull(stateMachine);
    this.context = checkNotNull(context);
    nodeLoopPool = Executors.newSingleThreadExecutor(runnable -> {
      var thread = new Thread(context.applyTo(runnable), String.format("node-%d-loop", id));
      thread.setDaemon(true);
      return thread;
    });
  }

  public void start() {
    stateMachine.start();
    // TODO reuse main thread, but without blocking test threads?
    CompletableFuture.runAsync(context.applyTo(this::loop), nodeLoopPool);
  }

  public StateMachine getStateMachine() {
    return stateMachine;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public boolean consume(Message<?> message) {
    return stateMachine.consume(message);
  }

  private void loop() {
    while (true) {
      consume(messageDeque.pool());
    }
  }
}
