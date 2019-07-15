package am.raft;

import cucumber.runtime.java.guice.ScenarioScoped;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertEquals;

@ScenarioScoped
public class DemoSteps {
  private int number;
  private int result;

  @Given("the number {int}")
  public void setNumber ( int number ) {
    this.number = number;
  }

  @When("I double it")
  public void doubleNumber() {
    this.result = number * 2;
  }

  @Then("the result is {int}")
  public void compareResult(int expected) {
    assertEquals(expected, result);
  }
}
