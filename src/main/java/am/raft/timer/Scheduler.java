package am.raft.timer;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.function.BiFunction;

@FunctionalInterface
public interface Scheduler extends BiFunction<Runnable, Duration, ScheduledFuture<?>> {
  /**
   * Same as {@link #apply(Object, Object)}
   */
  default ScheduledFuture<?> schedule(Runnable action, Duration delay) {
    return apply(action, delay);
  }
}
