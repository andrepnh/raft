package am.raft.queue;

import com.google.inject.AbstractModule;

public class QueueGuiceModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(CoalescableDeque.class).to(ThreadSafeCoalescableDeque.class);
    bind(CoalescableDequeWriter.class).to(ThreadSafeCoalescableDeque.class);
  }
}
