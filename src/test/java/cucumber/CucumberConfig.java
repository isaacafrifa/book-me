package cucumber;

import iam.bookme.Application;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@CucumberContextConfiguration
//@EnableTransactionManagement
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberConfig {

    @Before
    public void setUp() {
        // dummy method so cucumber recognises this class as glue and uses its context config
        System.out.println("CucumberConfig.setUp");
    }
}
