package pl.edu.agh;

import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import smile.Network;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.*;

public class App {
    private static final Map<String, List<String>> CLIENT_PROPERTIES =
            new HashMap<String, List<String>>() {{
                put("ClientAge", Arrays.asList("YoungClient", "MidAgeClient", "OldClient"));
                put("ClientMoney", Arrays.asList("PoorClient", "MidWealthClient", "RichClient"));
                put("ClientFamily", Arrays.asList("SingleClient", "FamilyClient"));
                put("ClientLocation", Arrays.asList("BigCity", "Village"));
            }};
    private static final List<String> LIST_OF_AVAILABLE_CARS =
            Arrays.asList("BugattiVeyron", "MacerattiGranTurismo", "Maybach_57S",
                          "HammerH3", "KiaCedd");

    public static void main( String[] args ) throws NoSuchFieldException, IllegalAccessException, URISyntaxException, IOException {
        Network network = configureApp();
        runApp(network);
    }

    private static Network configureApp() throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        System.loadLibrary("jsmile");

        String networkContent =
                IOUtils.toString(classLoader.getResourceAsStream("automobileadvisor.xdsl"));

        Network network = new Network();
        network.readString(networkContent);
        return network;
    }

    private static void runApp(Network network) {
        Scanner in = new Scanner(System.in);

        setClientProperties(network, in);

        network.updateBeliefs();

        for (String car : LIST_OF_AVAILABLE_CARS) {
            System.out.println("Result for car: " + car);
            for (int i = 0; i < network.getOutcomeCount(car); i++) {
                String outcomeName = network.getOutcomeId(car, i);
                double outcomeValue = network.getNodeValue(car)[i];
                System.out.println(outcomeName + " " + outcomeValue);
            }
            System.out.println("End of result of car:" + car);
        }
    }

    private static void setClientProperties(Network network, Scanner in) {
        for (String clientProperty : CLIENT_PROPERTIES.keySet()) {
            System.out.println("Choose property for: " + clientProperty);
            System.out.println(CLIENT_PROPERTIES.get(clientProperty));
            System.out.println("Write number to set property for value(starts from 0)");
            int propertyIndex = in.nextInt();
            network.setEvidence(clientProperty,
                    CLIENT_PROPERTIES.get(clientProperty).get(propertyIndex));
        }
    }
}
