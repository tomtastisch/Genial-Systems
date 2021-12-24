package javax.sys.launch.def.browser.plattform;

public enum OS {

    WIN(ProcessBuilderProperty.WINDOWS),
    UNIX(ProcessBuilderProperty.UNIX),
    LINUX(ProcessBuilderProperty.LINUX);

    public final Sniffer browser;

    OS(String... regex) {
        this.browser = new Sniffer(this, regex);
    }
}

interface ProcessBuilderProperty {
    String[] WINDOWS = {"cmd", "/c", "reg", "query",
            "\"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\Shell\\Associations\\UrlAssociations\\http\\UserChoice\"",
            "/v", "ProgId"};

    String[] UNIX = {"bash", "-c", "???"};
    String[] LINUX = {"bash", "-c", "xdg-settings", "get", "default-web-browser"};
}