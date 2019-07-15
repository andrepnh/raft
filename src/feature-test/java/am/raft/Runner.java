package am.raft;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(glue = "am.raft", features = "src/feature-test/resources", strict = true, tags = "not @ignore")
public class Runner {

}
