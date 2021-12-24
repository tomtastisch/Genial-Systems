package javax.sys.launch.def.browser;
/**
 * Copyright 2021 tom werner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.WebDriver;

import javax.sys.launch.def.browser.plattform.Sniffer;
import java.lang.reflect.InvocationTargetException;

/**
 *
 */
public @NotNull record GDriver(String browserName) {

    public static @Nullable WebDriver systemExplorer() {
        try {return new GDriver(Sniffer.systemBrowserName()).createDriverInstance();}
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



