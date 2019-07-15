package am.raft.timer;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

final class BasicTimerHandle implements TimerHandle {
  private final Scheduler scheduler;

  private Supplier<Duration> delaySupplier;

  private Runnable action;

  private ScheduledFuture<?> nextTrigger;

  BasicTimerHandle(Scheduler scheduler) {
    this.scheduler = checkNotNull(scheduler);
  }

  @Override
  public void program(Runnable action, Supplier<Duration> delaySupplier) {
    checkNotProgrammed();
    this.action = checkNotNull(action);
    this.delaySupplier = checkNotNull(delaySupplier);
  }

  @Override
  public void start() {
    checkIsProgrammed();
    nextTrigger = scheduler.schedule(action, delaySupplier.get());
  }

  @Override
  public void restart() {
    checkIsProgrammed();
    cancel();
    start();
  }

  @Override
  public void cancel() {
    checkState(nextTrigger != null, "Cannot cancel timer, there's no next trigger");
    nextTrigger.cancel(true);
    nextTrigger = null;
  }
  private void checkNotProgrammed() {
    checkState(this.action == null, "Timer action is effectively final and cannot be changed once set");
    checkState(this.delaySupplier == null,
        "Timer delay supplier is effectively final and cannot be changed once set");
  }

  private void checkIsProgrammed() {
    checkState(action != null, "Cannot start a timer if it's not programmed with an action");
    checkState(delaySupplier != null, "Cannot start a timer if it's not programmed with a delay supplier");
  }
}
