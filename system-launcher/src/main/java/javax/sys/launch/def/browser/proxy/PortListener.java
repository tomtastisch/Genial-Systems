package javax.sys.launch.def.browser.proxy;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 */
public class PortListener {

    /**
     *
     * @return stream of listed ports
     * @throws IOException
     */
    public static String listenPorts() throws IOException {
        Process p = Runtime.getRuntime().exec(new String[] { "lsof", "-i" });
        InputStream commandOutput = p.getInputStream();
        return IOUtils.toString(commandOutput, StandardCharsets.UTF_8.name());
    }

    /**
     *
     * @param firstPort
     * @param lastPort
     * @return
     */
    public static List<Integer> notAvailablePorts(int firstPort, int lastPort) {
        return new ArrayList<>(
                org.apache.commons.collections4.CollectionUtils.disjunction(
                    IntStream.rangeClosed(firstPort, lastPort).boxed().collect(Collectors.toList()),
                    availablePorts(firstPort, lastPort)));
    }

    /**
     *
     * @param firstPort
     * @param lastPort
     * @return
     */
    public static List<Integer> availablePorts(int firstPort, int lastPort) {
        return Arrays.stream(IntStream.rangeClosed(firstPort, lastPort).toArray())
                .filter(PortListener::isPortAvailable).boxed().collect(Collectors.toList());
    }

    /**
     * Check to see if a port is available.
     *
     * @param port -> the port to check for availability.
     */
    public static boolean isPortAvailable(int port) {
        try (var ss = new ServerSocket(port); var ds = new DatagramSocket(port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
