package javax.sys.launch.def.browser.plattform;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public record Sniffer(OS os, String[] regex) {

    public static @NotNull DriverInstance systemBrowser() {
        return Objects.requireNonNull(Arrays.stream(DriverInstance.values())
                .filter(driver -> driver.name().equals(systemBrowserName()))
                .findFirst().orElse(null));
    }

    public static @NotNull String systemBrowserName() {
        return Objects.requireNonNull(Arrays.stream(OS.values())
                .filter(os -> System.getProperty("os.name").toLowerCase().contains(os.name().toLowerCase()))
                .findFirst().orElse(null)).browser.name();
    }

    public @NotNull String name() {
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
