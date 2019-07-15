package am.raft.timer;

import am.raft.Context;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

class DefaultTimerFactory implements TimerFactory, AutoCloseable {
  private static final Logger LOG = LogManager.getLogger(DefaultTimerFactory.class);

  private final Context context;

  private final ScheduledExecutorService executor;

  @Inject
  public DefaultTimerFactory(@Assisted Context context) {
    this.context = checkNotNull(context);
    executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
      var thread = new Thread(runnable, String.format("node-%d-timer", context.getId()));
      thread.setDaemon(true);
      return thread;
    });
  }

  @Override
  public TimerHandle create() {
    // We don't want to expose the executor service to timers, but they need a way to schedule the tasks
    return new BasicTimerHandle(getScheduler());
  }

  /**
   * Release any resources associated with the factory, like thread pools. Calling other methods after this one
   * completes will result in exceptions.
   */
  @Override
  public void close() {
    executor.shutdownNow();
  }

  // TODO find another way to expose the executor service to test implementations
  Scheduler getScheduler() {
    // TODO is this instantiated multiple times?
    return (action, delay) -> {
      var handle = executor.schedule(context.applyTo(action), delay.toMillis(), TimeUnit.MILLISECONDS);
      LOG.debug("Will trigger in {}ms: {}", delay.toMillis(), action);
      return handle;
    };
  }
}
