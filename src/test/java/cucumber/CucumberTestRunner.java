package cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;

/**
 * This class is used to run the Cucumber tests.
 */

@ActiveProfiles("test")
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features",
        glue = "src/test/java/cucumber/steps",
        plugin = {"pretty", "json:target/cucumber-reports/Cucumber.json",
                "html:target/cucumber-reports/Cucumber.html"},
        monochrome = true)
public class CucumberTestRunner {
}
