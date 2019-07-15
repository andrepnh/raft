package am.raft.timer;

import am.raft.Context;
import com.google.inject.assistedinject.Assisted;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

class GatedTimerFactory implements TimerFactory, AutoCloseable {
  private final Semaphore permits = new Semaphore(0);

  private final Context context;

  private final DefaultTimerFactory delegate;

  @Inject
  public GatedTimerFactory(@Assisted Context context) {
    this.context = checkNotNull(context);
    this.delegate = new DefaultTimerFactory(context);
  }

  /**
   * Used to allow up to {@code amount} timer executions. Every timer execution will decrement the number of
   * allowed cycles remaining. Scheduled actions will remain blocked until there are cycles available or until
   * interrupted (e.g. {@link TimerHandle#cancel()} or {@link TimerHandle#restart()}).
   *
   * The future returned can be used to block execution until all cycles are triggered.
   */
  CompletableFuture<Void> allowCycles(int amount) {
    permits.release(amount);
    // TODO move away from common pool?
    return CompletableFuture.runAsync(context.applyTo(this::waitUntilPermitsConsumed));
  }

  private void waitUntilPermitsConsumed() {
    // TODO remove busy waiting
    while (permits.availablePermits() > 0) {
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        break;
      }
    }
  }

  @Override
  public TimerHandle create() {
    return new GatedTimerHandle(delegate.getScheduler());
  }

  @Override
  public void close() {
    delegate.close();
  }

  private class GatedTimerHandle implements TimerHandle {
    private final Logger log = LogManager.getLogger(GatedTimerHandle.class);

    private final TimerHandle delegate;

    GatedTimerHandle(Scheduler scheduler) {
      delegate = new BasicTimerHandle(scheduler);
    }

    @Override
    public void program(Runnable action, Supplier<Duration> delaySupplier) {
      checkNotNull(action);
      Runnable gatedAction = () -> {
        try {
          log.debug("Will try to acquire 1 of {} permits (susceptible to change until .acquire() call)",
              permits.availablePermits());
          permits.acquire();
          log.debug("Acquired 1 of {} permits (actual number could have changed since .acquire() call)",
              permits.availablePermits());
          action.run();
        } catch (InterruptedException e) {
          log.info("Interrupted while waiting for permit");
          // Someone called cancel or restart while execution was blocked
        }
      };
      delegate.program(gatedAction, delaySupplier);
    }

    @Override
    public void start() {
      delegate.start();
    }

    @Override
    public void restart() {
      delegate.restart();
    }

    @Override
    public void cancel() {
      delegate.cancel();
    }
  }
}
