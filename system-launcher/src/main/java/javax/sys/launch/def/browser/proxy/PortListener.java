package javax.sys.launch.def.browser.proxy;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class for listening to single or all available ports.
 * Used to read out and assign already used port to an application or to
 * determine still free ports, which can be used for further operation.
 *
 * @ToDo: Shall be used later to determine the ports in which vm or
 *        server are running, if necessary to check localhost related pages
 */
public class PortListener {

    /**
     * @return stream of listed ports
     * @throws IOException
     */
    public static String listenPorts() throws IOException {
        return org.apache.commons.io.IOUtils.toString(
                Runtime.getRuntime().exec(new String[] { "lsof", "-i" }).getInputStream(),
                StandardCharsets.UTF_8.name());
    }

    /**
     * @param firstPort -> first port to read
     * @param lastPort  -> last port to read
     * @return all not available ports within the given area
     */
    public static List<Integer> notAvailablePorts(int firstPort, int lastPort) {
        return new ArrayList<>(org.apache.commons.collections4.CollectionUtils.disjunction(
                    IntStream.rangeClosed(firstPort, lastPort).boxed().collect(Collectors.toList()),
                    availablePorts(firstPort, lastPort)));
    }

    /**
     * @param firstPort -> first port to read
     * @param lastPort  -> last port to read
     * @return all available ports within the given area
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
