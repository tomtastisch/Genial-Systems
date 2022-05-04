package javax.sys.launch.def.utils;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.*;

/**
 * Class for listening to single or all available ports.
 * Used to read out and assign already used port to an application or to
 * determine still free ports, which can be used for further operation.
 */
public final class PortUtils {

    private static final String SEPARATOR = ",";
    private static final String[] COMMAND = new String[] { "lsof", "-i" };

    public static void main(String[] args) throws IOException {
        //System.out.println(PortUtils.usedPorts(0,10000));
        System.out.println(PortUtils.getPortApplication(8081, 2022));
    }

    /**
     *
     * @param ports -> Ports of the applications
     * @return a list of the applications of the given ports
     */
    public static @Nullable Map<String, String> getPortApplication(int... ports) {

        final List<String> runningApps = getRunningApps();

        if(ports == null || ports.length == 0)
            throw new RuntimeException("At least one special port must be specified.");

        Arrays.stream(ports).forEach(port -> {
            String pid = getPortInfo(listen(port)).get("PID").stream().toList().get(0);



        });
        return null;
    }

    /**
     *
     * @param ports -> port for which the information is to be determined
     * @return the information of this port
     */
    private static Map<String, Collection<String>> getPortInfo(@NotNull List<String> ports) {
        final List<String> keys  = Arrays.stream(cleanString(ports.remove(0)).split(SEPARATOR)).toList();
        final MultiValuedMap<String, String> portMap = new ArrayListValuedHashMap<>();

        // Loop through list of information from port listener
        ports.forEach(vars -> {
            // Add the key (header) and column values (content) into the multimap
            for(int idx = 0; idx < keys.size(); idx++)
                portMap.put(keys.get(idx), Arrays.stream(cleanString(vars).split(SEPARATOR)).toList().get(idx));
        });
        return portMap.asMap();
    }

    /**
     * @return the application that holds the given port occupied
     */
    @Contract(pure = true)
    @Deprecated public static @Nullable String getRunningAppOf(int pid) {

        return null;
    }

    /**
     * @ToDo: Complete implementation of the information collection.
     * @return All applications that currently occupy the current ports (Provided they occupy one or more ports)
     */
    public static @Nullable List<String> getRunningApps() {
        try {
            String process;
            // getRuntime: Returns the runtime object associated with the current Java application.
            // exec: Executes the specified string command in a separate process.
            Process p = Runtime.getRuntime().exec("ps -few");

            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((process = input.readLine()) != null) {
                System.out.println(process); // <-- Print all Process here line
                // by line
            }
            input.close();

        } catch (Exception err) {
            err.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @return list of all information about the ports and their scope
     */
    @Deprecated private static List<String> listen() {
        return exec(COMMAND);
    }

    /**
     *
     * @param port -> port to be listened
     * @return information of this port
     */
    private static List<String> listen(int port) {
        String[] command = COMMAND.clone();
        command[command.length - 1] = command[command.length - 1] + ":" + port;
        return exec(command);
    }

    /**
     * @param firstPort -> first port to read
     * @param lastPort  -> last port to read
     * @return all not available ports within the given area
     */
    @Contract("_, _ -> new")
    public static @NotNull List<Integer> usedPorts(int firstPort, int lastPort) {
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
                .filter(PortUtils::isPortAvailable)
                .boxed().collect(Collectors.toList());
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

    /**
     *
     * @param command -> command to be run
     * @return the result returned by the executed commands
     */
    private static List<String> exec(String... command) {
        try { return IOUtils.readLines(Runtime.getRuntime().exec(command).getInputStream(),
                StandardCharsets.UTF_8.name());
        } catch(IOException e) { throw new RuntimeException(""); }
    }

    private static String cleanString(@NotNull String s) {
        return StringUtils.join(Arrays.stream(s.split("\s"))
                .filter(col -> !col.replace("\s", "").equals(""))
                .toArray(), SEPARATOR);
    }
}
