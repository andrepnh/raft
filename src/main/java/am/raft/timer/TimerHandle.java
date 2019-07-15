package am.raft.timer;

import java.time.Duration;
import java.util.function.Supplier;

public interface TimerHandle {
  void program(Runnable action, Supplier<Duration> delaySupplier);

  void start();

  void restart();

  void cancel();
}
