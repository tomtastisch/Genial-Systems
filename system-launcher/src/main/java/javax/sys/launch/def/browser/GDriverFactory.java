package javax.sys.launch.def.browser;
/**
 * Copyright 2021 tom werner
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.sys.launch.def.browser.plattform.DriverInstance;
import javax.sys.launch.def.browser.plattform.Sniffer;
import java.lang.reflect.InvocationTargetException;

/**
 * System driver, which automates and system-specific performs
 * the specified Selenium Driver, do without preparations to meet.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public @NotNull record GDriverFactory(@Nullable DriverInstance instance) {

    static Logger l = LoggerFactory.getLogger(GDriverFactory.class);
    /**
     * Analyzes the OS and its default web browser to play this as a stable class.
     * @return standard selenium driver, which has been
     *          stored in the system settings of the OS
     */
    public static @Nullable WebDriver systemExplorer() {
        try {
            l.info("start of creation and run a instance of the default web-driver");
            /* Creates a new DriverFactory instance with the system default
             * web-driver as parameter. */
            return new GDriverFactory(Sniffer.systemBrowser()).createDriverInstance();
        } catch (Exception e) {
            l.error(String.valueOf(e.fillInStackTrace()));
            return null;
        }
    }

    /**
     * creates a web-driver instance of the default browser of the current OS
     * @return a web-driver instance of the system default driver
     */
    @NotNull WebDriver createDriverInstance() throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {

        l.info("manage web driver components and install feature of this");
        //create protocol handshake
        WebDriverManager manage = WebDriverManager.getInstance(DriverManagerType.valueOf(instance.name()));
        manage.setup();
        //create driver instance

        l.info("creates a instance of the default web-driver and performs " +
                "this using the installed features");
        return (WebDriver) ((java.lang.reflect.Constructor<?>)
                Class.forName(manage.getDriverManagerType().browserClass()).getConstructor())
                .newInstance();
    }
}



