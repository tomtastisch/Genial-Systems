package javax.sys.launch.def.browser;

import net.jodah.failsafe.internal.util.Assert;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import javax.sys.launch.def.browser.plattform.SystemExplorer;
import java.util.Objects;

public class GDriverTest {

    @Test public void build_driver() {
        try (SystemExplorer explorer = GDriverFactory.systemExplorer()) {
            WebDriver driver = explorer.createDriverInstance();

            assert Objects.nonNull(driver);
        } catch(Exception e) {
            e.printStackTrace();
        }
        //check if is the instance not null
        //Assert.notNull(Objects.requireNonNull(driver), "j-driver-test-failed");
    }
}
