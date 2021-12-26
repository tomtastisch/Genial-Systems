package javax.sys.launch.def.browser;

import org.junit.jupiter.api.Test;

import javax.sys.launch.def.browser.plattform.SystemExplorer;

public class GDriverTest {

    @Test public void build_driver() {
        try (SystemExplorer wdi = GDriverFactory.systemExplorer()) {
            wdi.createDriverInstance();
        } catch(Exception e) {
            e.printStackTrace();
        }
        //check if is the instance not null
        //Assert.notNull(Objects.requireNonNull(driver), "j-driver-test-failed");
    }
}
