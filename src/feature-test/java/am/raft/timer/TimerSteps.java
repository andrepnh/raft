package am.raft.timer;

import am.raft.fixture.Environment;
import cucumber.runtime.java.guice.ScenarioScoped;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ScenarioScoped
public class TimerSteps {
  private static final Logger LOG = LogManager.getLogger(TimerSteps.class);

  private final Environment environment;

  @Inject
  public TimerSteps(Environment environment) {
    this.environment = environment;
  }

  @When("I let each timer run for {int} cycle(s)")
  public void runTimers(int cycles) throws InterruptedException, ExecutionException, TimeoutException {
    CompletableFuture[] pendingCycles = environment.getClusterNodes()
        .stream()
        .map(node -> (GatedTimerFactory) node.getStateMachine().getTimerFactory()) // TODO remove this cast
        .map(factory -> factory.allowCycles(cycles))
        .toArray(CompletableFuture[]::new);
    LOG.info("Will wait until each of the {} timers go through {} cycles",
        environment.getClusterNodes().size(), cycles);
    CompletableFuture.allOf(pendingCycles).get(5, TimeUnit.SECONDS);
  }
}
