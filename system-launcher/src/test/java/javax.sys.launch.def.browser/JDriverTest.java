package javax.sys.launch.def.browser;

import net.jodah.failsafe.internal.util.Assert;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import java.util.Objects;

public class JDriverTest {

    @Test public void build_driver() {
        WebDriver driver = JDriver.systemExplorer();
        //check if is the instance not null
        Assert.notNull(Objects.requireNonNull(driver), "j-driver-test-failed");
    }

}
