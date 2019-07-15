package am.raft;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class BootstrapperModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new FactoryModuleBuilder()
        .implement(Bootstrapper.class, Bootstrapper.class)
        .build(BootstrapperFactory.class));
    install(new FactoryModuleBuilder()
        .implement(Context.class, Context.class)
        .build(ContextFactory.class));
  }
}
