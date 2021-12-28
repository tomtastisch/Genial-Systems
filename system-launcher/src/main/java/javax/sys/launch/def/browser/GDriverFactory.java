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
package javax.sys.launch.def.browser;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.sys.launch.def.browser.plattform.DriverInstance;
import javax.sys.launch.def.browser.plattform.ExplorerValueMapper;
import javax.sys.launch.def.browser.plattform.Sniffer;
import javax.sys.launch.def.browser.plattform.SystemExplorer;
import java.lang.ref.Cleaner;
import java.nio.file.Paths;
import java.util.Random;

/**
 * System driver, which automates and system-specific performs
 * the specified Selenium Driver, do without preparations to meet.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public @NotNull record GDriverFactory(@NotNull DriverInstance instance, long id, boolean autoClose)
        implements SystemExplorer, ExplorerValueMapper {

    private static final Cleaner cleaner = Cleaner.create();

    /**
     * Analyzes the OS and its default web browser to play this as a stable class.
     *
     * @return standard selenium driver, which has been
     * stored in the system settings of the OS
     */
    public static @NotNull SystemExplorer systemExplorer() {
        LOGGER.info("start of creation and run a instance of the default web-driver");
        /* Creates a new DriverFactory instance with the system default
         * web-driver as parameter. */
        return new GDriverFactory(Sniffer.systemBrowser(),
                new Random().nextLong(), true);
    }

    @Override public WebDriver createDriverInstance() {
        try {
            LOGGER.info("manage web driver components and install feature of this");
            WebDriverManager manage = WebDriverManager.getInstance(DriverManagerType.valueOf(instance.name()));
            /* Set download path for the driver.jar file*/
            manage.cachePath(tmpPath).setup();
            LOGGER.info("creates a  instance of the default web-driver and performs this using the installed features");
            /* Creates a new WebDriver instance by inserting the collected
             * data to the required digits via reflection and thus can be
             * adjusted a constructor call. */
            WebDriver driver = (WebDriver) ((java.lang.reflect.Constructor<?>)
                    /* Create a Web driver instance using the Manager class and its previously
                     * transferred browser name */
                    Class.forName(manage.getDriverManagerType().browserClass()).getConstructor())
                    /* Realization of instantiation */
                    .newInstance();
            /* Add the created driver into the queue. */
            queue.put(id, driver);
            /* Store the generated path for later processing and
             * deleting the created files when downloading the JVM. */
            queue.put(id, manage.getDownloadedDriverPath());
            LOGGER.info(driver + " is created and was admitted to the queue.");
            return driver;
        } catch (Exception e) {
            LOGGER.error(String.valueOf(e));
            return null;
        }
    }

    @Override public void close() {
        if (autoClose) {
            queue.get(id).forEach(element -> {
                if (element instanceof WebDriver driver) {
                    clean(driver, driver::quit);
                } else if (Paths.get(element.toString()).toFile().exists()) {
                    clean(element, () -> Paths.get(element.toString()).toFile().deleteOnExit());
                }
            });
        }
    }

    @Override public void clean(Object o, Runnable r) {
        LOGGER.info("destroy/delete " + (o.toString().contains(":") ?
                o.toString().replace(": ", " [") : "files from [" + o)
                + "] instance and clean with gc.");
        cleaner.register(o, r);
        System.gc();
    }
}



