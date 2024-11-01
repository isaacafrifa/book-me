package cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.springframework.test.context.ActiveProfiles;

import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * This class is used to run the Cucumber tests.
 */

@ActiveProfiles("cucumber")
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@SelectPackages({"cucumber.steps"})
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value =
                """
                        pretty,
                        html:target/cucumber-reports/report.html,
                        json:target/cucumber-reports/report.json""")

public class CucumberTestRunner {
}
