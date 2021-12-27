package javax.sys.launch.def.browser.plattform;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Sniffer class to determine the required information
 * about the system and individual default setting requirements
 */
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public @NotNull record Sniffer(@NotNull OS os, @NotNull String[] regex) {
    /**
     * Find the name of the system web-browser
     * @return {@link DriverInstance#name() name of system web-browser}
     */
    public static @NotNull DriverInstance systemBrowser() {
        return Objects.requireNonNull(Arrays.stream(DriverInstance.values())
                .filter(driver -> driver.name().equals(systemBrowserName()))
                .findFirst().orElse(null));
    }

    /**
     * For error avoidance, both name spends are compared with the
     * help of {@link String#toLowerCase() lower case}
     * @return name of the System-default-browser
     * @see #name()
     */
    public static @Nullable String systemBrowserName() {
        return Objects.requireNonNull(Arrays.stream(OS.values())
                .filter(os -> System.getProperty("os.name")
                        /* Equals the name of the system-property return
                         * with the name of value os-value name.*/
                        .toLowerCase().contains(os.name().toLowerCase()))
                .findFirst().orElse(null)).browser.name();
    }

    /**
     * Compares the name of existing web-driver values with the system default web driver name <br>
     * used regex for find the name
     * @return regex command for find the system default web-browser
     */
    public @Nullable String name() {
        try {// registration where we find the default browser
            return StringUtils.join(
                    new BufferedReader(new InputStreamReader(new ProcessBuilder(regex)
                            .start().getInputStream())).lines()
                            .filter(Objects::nonNull)
                            .map(e -> Arrays.stream(DriverInstance.values())
                                    .filter(driver -> e.matches("(.*)" + driver.get().regex() + "(.*)"))
                                    .map(Enum::name)
                                    .findFirst().orElse("")
                            ).collect(Collectors.toList()), "");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
