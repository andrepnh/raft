package am.raft.timer;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class TestTimerGuiceModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new FactoryModuleBuilder()
        .implement(TimerFactory.class, GatedTimerFactory.class)
        .build(TimerFactoryProvider.class));
  }
}
