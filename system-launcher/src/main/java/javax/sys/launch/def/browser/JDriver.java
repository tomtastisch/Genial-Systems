package javax.sys.launch.def.browser;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.WebDriver;

import javax.sys.launch.def.browser.plattform.Sniffer;
import java.lang.reflect.InvocationTargetException;

@NotNull record JDriver(String browserName) {

    private static WebDriver DRIVER;

    public static @Nullable WebDriver systemExplorer() {
        try {return new JDriver(Sniffer.systemBrowserName()).createDriverInstance();}
        catch (Exception e) {return null;}
    }

    @NotNull WebDriver createDriverInstance() throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        //create protocol handshake
        WebDriverManager manage = WebDriverManager.getInstance(DriverManagerType.valueOf(browserName));
        manage.setup();
        //create driver instance
        return (WebDriver) ((java.lang.reflect.Constructor<?>)
                Class.forName(manage.getDriverManagerType().browserClass()).getConstructor())
                .newInstance();
    }
}



