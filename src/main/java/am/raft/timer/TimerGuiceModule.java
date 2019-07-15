package am.raft.timer;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class TimerGuiceModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new FactoryModuleBuilder()
        .implement(TimerFactory.class, DefaultTimerFactory.class)
        .build(TimerFactoryProvider.class));
  }
}
