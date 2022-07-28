package javax.sys.launch.def.browser;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import org.system.factory.launcher.browser.GDriverFactory;
import org.system.factory.launcher.browser.plattform.SystemExplorer;
import java.util.List;
import java.util.Objects;

public class GDriverTest {

    @Test public void build_driver() {
        try (SystemExplorer<WebDriver> explorer = GDriverFactory.systemExplorer()) {
            WebDriver driver = explorer.createDriverInstance();
            assert Objects.nonNull(driver);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test public void build_driver_list() {
        try (SystemExplorer<WebDriver> explorer = GDriverFactory.systemExplorer()) {
            List<WebDriver> drivers = explorer.createDriverInstances(3);
            /* Check are the objects non-null*/
            drivers.forEach(e -> {assert Objects.nonNull(e);});
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
