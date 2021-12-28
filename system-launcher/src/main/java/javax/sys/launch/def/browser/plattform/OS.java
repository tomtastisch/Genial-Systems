package javax.sys.launch.def.browser.plattform;

enum OS {

    WIN(ProcessBuilderProperty.WINDOWS),
    UNIX(ProcessBuilderProperty.UNIX),
    LINUX(ProcessBuilderProperty.LINUX);

    /** returns the Sniffer class as browser-object */
    public final Sniffer browser;

    OS(String... regex) {
        this.browser = new Sniffer(this, regex);
    }

    /**
     * <code>CMD/BASH</code> - Commands <br>
     * <pre>for determining the required data summarized in an interface</pre>
     */
    interface ProcessBuilderProperty {
        /* Windows system command */
        String[] WINDOWS = {"cmd", "/c", "reg", "query",
                "\"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\Shell\\Associations\\UrlAssociations\\http\\UserChoice\"",
                "/v", "ProgId"};
        /* UNIX/MACOS system command */
        @Deprecated String[] UNIX = {"bash", "-c", "???"};
        /* Linux's system command */
        String[] LINUX = {"bash", "-c", "xdg-settings", "get", "default-web-browser"};

        /** @return a command for the system */
        String command();
    }
}