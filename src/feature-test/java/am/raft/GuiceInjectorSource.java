package am.raft;

import am.raft.queue.QueueGuiceModule;
import am.raft.timer.TestTimerGuiceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import cucumber.api.guice.CucumberModules;
import cucumber.runtime.java.guice.InjectorSource;

public class GuiceInjectorSource implements InjectorSource {
  @Override
  public Injector getInjector() {
    return Guice.createInjector(
        CucumberModules.createScenarioModule(),
        new BootstrapperModule(),
        new QueueGuiceModule(),
        new TestTimerGuiceModule());
  }
}
