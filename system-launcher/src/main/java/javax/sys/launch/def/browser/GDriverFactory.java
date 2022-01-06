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
import javax.sys.launch.def.utils.ExceptionUtils;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * System driver, which automates and system-specific performs
 * the specified Selenium Driver, do without preparations to meet.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public @NotNull record GDriverFactory(@NotNull DriverInstance instance, long id, boolean autoClose)
        implements SystemExplorer<WebDriver>, ExplorerValueMapper {

    /**
     * The specified object is transferred to the internal list of objects assigned
     * to the ID of the object. Subsequently, the object is given back again.
     * This is used to prevent the boiler plate code and enables writing to individuals.
     * @param element the given element to add to the list
     * @param <E> the element
     * @return the given element self
     */
    private <E extends WebDriver> E mappedObject(E element) {
        queue.put(id, element);
        return element;
    }

    /**
     * Analyzes the OS and its default web browser to play this as a stable class.
     * @return standard selenium driver, which has been
     * stored in the system settings of the OS
     */
    public static @NotNull SystemExplorer<WebDriver> systemExplorer() {
        LOGGER.info("start of creation and run a instance of the default web-driver");
        /* Creates a new DriverFactory instance with the system default
         * web-driver as parameter. */
        return new GDriverFactory(Sniffer.systemBrowser(),
                new Random().nextLong(), true);
    }

    @Override public @NotNull List<WebDriver> createDriverInstances(int count) {
        return Arrays.asList(Arrays.asList(new WebDriver[count])
                .parallelStream()
                .map(gdf -> new GDriverFactory(instance, new Random().nextLong(), autoClose))
                .map(wd -> mappedObject(ExceptionUtils.unthrow(wd::createDriverInstance, 1000)))
                .toArray(WebDriver[]::new));
    }

    @Override public WebDriver createDriverInstance() throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {

        LOGGER.info("manage web driver components and install feature of this");
        WebDriverManager manage = WebDriverManager.getInstance(DriverManagerType.valueOf(instance.name()));
        /* Set download path for the driver.jar file*/
        manage.cachePath(tmpPath).setup();
        LOGGER.info("creates a  instance of the default web-driver and performs this using the installed features");
        /* Creates a new WebDriver instance by inserting the collected
         * data to the required digits via reflection and thus can be
         * adjusted a constructor call. */
        WebDriver driver = mappedObject((WebDriver) ((java.lang.reflect.Constructor<?>)
                /* Create a Web driver instance using the Manager class and its previously
                 * transferred browser name */
                Class.forName(manage.getDriverManagerType().browserClass()).getConstructor())
                /* Realization of instantiation */
                .newInstance());
        /* Store the generated path for later processing and
         * deleting the created files when downloading the JVM.
         * -> Set delete on exit */
        Paths.get(manage.getDownloadedDriverPath()).toFile().deleteOnExit();

        LOGGER.info(driver + " is created and was admitted to the queue.");
        return driver;
    }

    @Override public void close() {
        if (autoClose) queue.get(id).forEach(driver -> clean(driver, driver::quit));
    }

    @Override public void clean(Object o, Runnable r) {
        LOGGER.info("destroy/delete " + (o.toString().contains(":") ?
                o.toString().replace(": ", " [") : "files from [" + o)
                + "] instance and clean with gc.");
        cleaner.register(o, r);
        System.gc();
    }
}



