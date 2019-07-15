package am.raft.timer;

import am.raft.Context;

public interface TimerFactoryProvider {
  TimerFactory provide(Context context);
}
