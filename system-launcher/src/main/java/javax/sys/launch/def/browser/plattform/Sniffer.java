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
     *
     * @return
     */
    public static @NotNull DriverInstance systemBrowser() {
        return Objects.requireNonNull(Arrays.stream(DriverInstance.values())
                .filter(driver -> driver.name().equals(systemBrowserName()))
                .findFirst().orElse(null));
    }

    /**
     *
     * For error avoidance, both name spends are compared with the
     * help of {@link String#toLowerCase() lower case}
     * @return name of the System-default-browser
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
     *
     * @return
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
        }
        return null;
    }
}
